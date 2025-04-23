import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FirstPass {

    public static class CustomerCountMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text customer = new Text();

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString();
            String[] parts = line.split("\\t");
            if (parts.length < 2) return;
            StringTokenizer itr = new StringTokenizer(parts[1]);
            while (itr.hasMoreTokens()) {
                customer.set(itr.nextToken());
                context.write(customer, one);
            }
        }
    }

    public static class CustomerCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        private int threshold;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // Lấy threshold từ Configuration
            Configuration conf = context.getConfiguration();
            threshold = conf.getInt("customer.threshold", 1); // giá trị mặc định là 1
        }

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            // Chỉ ghi ra nếu sum >= threshold
            if (sum >= threshold) {
                result.set(sum);
                context.write(key, result);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: FrequentCustomersPass1 <input path> <output path> <threshold>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        // Truyền threshold vào Configuration
        int threshold = Integer.parseInt(args[2]);
        conf.setInt("customer.threshold", threshold);

        Job job = Job.getInstance(conf, "Frequent Customers Pass 1");
        job.setJarByClass(FirstPass.class);
        job.setMapperClass(CustomerCountMapper.class);
        job.setReducerClass(CustomerCountReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

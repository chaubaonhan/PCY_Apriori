import java.io.*;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SecondPass {

    public static class PairMapper extends Mapper<Object, Text, Text, IntWritable> {
        private Set<String> frequentCustomers = new HashSet<>();
        private final static IntWritable one = new IntWritable(1);
        private Text pairKey = new Text();

        @Override
        protected void setup(Context context) throws IOException {
            // Đọc tập L1 từ Distributed Cache hoặc file bên ngoài
            Configuration conf = context.getConfiguration();
            Path[] cacheFiles = context.getLocalCacheFiles();
            if (cacheFiles != null && cacheFiles.length > 0) {
                for (Path path : cacheFiles) {
                    BufferedReader reader = new BufferedReader(new FileReader(path.toString()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] tokens = line.trim().split("\\s+");
                        if (tokens.length >= 1) {
                            frequentCustomers.add(tokens[0]);
                        }
                    }
                    reader.close();
                }
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // Input: date \t customer1 customer2 customer3 ...
            String[] tokens = value.toString().split("\\t");
            if (tokens.length < 2) return;

            String[] customers = tokens[1].trim().split("\\s+");
            Set<String> validCustomers = new TreeSet<>();  // TreeSet để tự động sort

            for (String customer : customers) {
                if (frequentCustomers.contains(customer)) {
                    validCustomers.add(customer);
                }
            }

            // Sinh tất cả các pair từ validCustomers
            List<String> list = new ArrayList<>(validCustomers);
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    pairKey.set(list.get(i) + "," + list.get(j));
                    context.write(pairKey, one);
                }
            }
        }
    }

    public static class PairReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        private int threshold;

        @Override
        protected void setup(Context context) throws IOException {
            Configuration conf = context.getConfiguration();
            threshold = conf.getInt("pair.threshold", 2);  // default là 2
        }

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            if (sum >= threshold) {
                result.set(sum);
                context.write(key, result);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("Usage: SecondPass <input path> <output path> <L1 file> <pair threshold>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        conf.setInt("pair.threshold", Integer.parseInt(args[3]));

        Job job = Job.getInstance(conf, "Second Pass - Frequent Pairs");
        job.setJarByClass(SecondPass.class);
        job.setMapperClass(PairMapper.class);
        job.setReducerClass(PairReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Add L1 file (frequent 1-items) to distributed cache
        job.addCacheFile(new Path(args[2]).toUri());

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

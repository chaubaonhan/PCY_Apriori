import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class GroupShopping {

    public static class FrequentCustomersMapper extends Mapper<Object, Text, Text, Text> {
        private Text dateKey = new Text();
        private Text member = new Text();
        private boolean isHeader = true;

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] tokens = line.split(",");
            
            if (isHeader) {
                isHeader = false;
                return;
            }
            
            if (tokens.length >= 2) {
                member.set(tokens[0].trim());
                dateKey.set(tokens[1].trim());
                context.write(dateKey, member);
            }
        }
    }

    public static class FrequentCustomersReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            Set<String> customerSet = new HashSet<>();
            for (Text val : values) {
                customerSet.add(val.toString());
            }
            StringBuilder sb = new StringBuilder();
            for (String customer : customerSet) {
                sb.append(customer).append(" ");
            }
            context.write(key, new Text(sb.toString().trim()));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: FrequentCustomers <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Frequent Customers");
        job.setJarByClass(GroupShopping.class);
        job.setMapperClass(FrequentCustomersMapper.class);
        job.setReducerClass(FrequentCustomersReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

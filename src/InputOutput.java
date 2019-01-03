
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InputOutput {
	
	//readInput函数的作用是把txt文件inputFileName按行存入String类型的数组
	public String[] readInput(String inputFileName)
	{
		List<String> ret = new ArrayList<String>();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
			String temp;
			while((temp = br.readLine()) != null)
			{
				ret.add(temp);
			}
			br.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		String[] fileString = new String[ret.size()];
		return (String[]) ret.toArray(fileString);
	}
	
	//writeOutput函数的作用是将String类型数组outputContent的内容写入文件outputFileName中
	public void writeOutput(String[] outputContent,String outputFileName)
	{
		//将outputContent中的内容写入文件outputFileName中
		File f = new File(outputFileName);
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(int i = 0; i < outputContent.length; i++)
			{
				bw.write(outputContent[i]);
				bw.newLine();
			}
			bw.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

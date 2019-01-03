
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

//文本预处理，使用IKAnalyzer库进行中文分词、去停用词
public class TextPreProcess {
	
	//该函数输入是InputDocs，返回分词后的结果
	public String[] preProcessMain(String[] InputDocs) throws IOException
	{
		Set<String> stopWordSet = new HashSet<String>();
		String stopWord = null;
		//读入停用词文件
		try
		{
			BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream("data/stopWordSet.txt")));
			while((stopWord = StopWordFileBr.readLine()) != null)
			{
				stopWordSet.add(stopWord);
			}
			StopWordFileBr.close();
			//System.out.println(stopWordSet);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		String[] OutputDocs = new String[InputDocs.length];
		String row = "";
		String t = null;
		int count = 0;
		for(int i = 0; i < InputDocs.length; i++)
		{
			row += InputDocs[i].substring(0, 1);//下标0的字符，该行记录的类别
			t = InputDocs[i].substring(2);//截取从下标2往后的字符串
			
			//第二个参数决定是否使用最大词长切分
			IKSegmentation ikSeg = new IKSegmentation(new StringReader(t), true);
			Lexeme l = null;//单词
			while((l = ikSeg.next()) != null)
			{
				if(l.getLexemeType() == Lexeme.TYPE_CJK_NORMAL)
				{
					//去除停用词
					if(stopWordSet.contains(l.getLexemeText()))
					{
						continue;
					}
					row += '|' + l.getLexemeText();
				}
			}
			if(row.length() == 1)//没有有意义的单词，只有一个标签
			{
				row = "";
				continue;
			}
			OutputDocs[count++] = row;
			row = "";
		}
		String[] OutputDocs2 = new String[count];
		System.arraycopy(OutputDocs, 0, OutputDocs2, 0, count);
//		System.out.println(OutputDocs2.length);
//		System.out.println(InputDocs.length);
		
		return OutputDocs2;
	}
}

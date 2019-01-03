import java.util.ArrayList;
import java.util.List;

public class TermEasyRepresent {
	//便于先做特征选择，再做特征抽取
	
	List<String> terms = new ArrayList<String>();//程序使用的特征词典
	
	public String[] TermEasyRepresentMain(String[] segmentDocs, String[] termDic)
	{
		String[] result = new String[segmentDocs.length];
		//把所有特征词加入特征词典wordsIndex中
		for(int i = 0; i < termDic.length; i++)
		{
			terms.add(termDic[i]);
		}
		for(int i = 0; i < segmentDocs.length; i++)
		{
			String[] words = segmentDocs[i].split("([|])");
			String s = words[0];
			for(int j = 1; j < words.length; j++)
			{
				if(terms.contains(words[j]))
				{
					s += "|" + words[j];
				}
			}
			result[i] = s;
		}
		
		return result;
	}
}

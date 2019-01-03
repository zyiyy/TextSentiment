import java.util.List;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

//基于文档频率DF的特征选择方法
public class DFTermSelect {
	public String[] TermDictionaryMain(String[] initTerms)
	{
		String label = initTerms[0].substring(0, 1);//label表示这条记录的类别0、1、2
		List<String> allUniques = new ArrayList<String>();
		ArrayList<Integer> perLabelTermsNum =  new ArrayList<Integer>();//存各个类别的特征词个数
		int startTermsNum = 0;//记录原始特征词个数，包括重复的
		int i = 0;
		while(i < initTerms.length)
		{
			//把该类别所有特征词不重复的存入一个List
			List<String> uniques = new ArrayList<String>();
			
			while(label.compareTo(initTerms[i].substring(0, 1)) == 0)
			{
				String[] terms = initTerms[i].split("([|])");//terms存该条记录的所有特征词
				startTermsNum += terms.length;
				for(int j = 1; j < terms.length; j++)
				{
					if(!uniques.contains(terms[j]))
					{
						uniques.add(terms[j]);
					}
				}
				if(i < initTerms.length - 1)
				{
					i++;
				}
				else
				{
					break;
				}
			}
			//一个uniques列表存的是一个类别的特征词，不可能重复
			allUniques.addAll(uniques);//allUniques中不同类别的特征词可能重复
			perLabelTermsNum.add(uniques.size());
			if(i < initTerms.length - 1)
			{
				label = initTerms[i].substring(0, 1);
			}
			else
			{
				break;
			}
		}
		System.out.println("all labels:" + perLabelTermsNum.size());
		System.out.println(perLabelTermsNum);
		System.out.println("TermsNum:" + startTermsNum);//去重前特征词个数
		System.out.println("UniqueTermsNum:" + allUniques.size());//去重后特征词个数
//		for(int n = 0; n < allUniques.size(); n++)
//		{
//			System.out.println(allUniques.get(n));
//		}
		
		int m = 0;//当前类别
		int start = 0;//各类词的开始位置
		int k = 0;//当前记录的索引
		int[] docFreq = new int[allUniques.size()];//开一个总特征词数大小的数组，用于存放所有词的文档频率
		int termIndex;
		while(m < perLabelTermsNum.size())//循环所有的类别
		{
			int termsNum = perLabelTermsNum.get(m);//取第m类的特征词总个数
			//统计各类中的词在所属类中的DF(LocalDF)
			Dictionary<Object, Object> wordsIndex = new Hashtable<Object, Object>();//存储特征词的数据结构
			for(int n = start; n < start + termsNum; n++)
			{
				AddElement(wordsIndex, allUniques.get(n), n);
			}
			System.out.println("dictionary" + m + ":" + wordsIndex.size());
			start += termsNum;
			
			//扫描该类的所有记录
			while(m == Integer.parseInt(initTerms[k].substring(0, 1)))
			{
				String curDoc = initTerms[k];
				String[] terms = curDoc.split("([|])");
				
//				List<String> termsUniques = new ArrayList<String>();
//				for(int j = 1; j < terms.length; j++)
//				{
//					if(!termsUniques.contains(terms[j]))
//					{
//						termsUniques.add(terms[j]);
//					}
//				}
//				for(int j = 0; j < termsUniques.size(); j++)//只考虑出现与否，不考虑多次出现
//				{
//					Object index = wordsIndex.get(termsUniques.get(j));
//					if(index != null)
//					{
//						termIndex = (Integer)index;
//						docFreq[termIndex]++;
//					}
//				}
				for(int j = 1; j < terms.length; j++)//考虑一条记录中出现重复的特征词的情况
				{
					Object index = wordsIndex.get(terms[j]);//该特征词在词典中的索引
					if(index != null)
					{
						termIndex = (Integer)index;
						docFreq[termIndex]++;
					}
				}
				
				if(k < initTerms.length - 1)
				{
					k++;
				}
				else
				{
					break;
				}
			}
			//下一个类别
			m++;
		}	
//		for(int j = 0; j < allUniques.size(); j++)
//		{
//			System.out.println(allUniques.get(j)+ ":" + docFreq[j]);
//		}
		
		//取超过阈值的所有特征词形成新的特征词典
		List<String> DFterms = new ArrayList<String>();
		int threshold = 2;//DF阈值，可调
		int firstNums = perLabelTermsNum.get(0);
		//第一类所有超过阈值的词直接加入DF词典
		for(int j = 0; j < firstNums; j++)
		{
			if(docFreq[j] > threshold)
			{
				DFterms.add(allUniques.get(j));
			}
		}
		//剩余类所有超过阈值的词不重复加入DF词典
		for(int j = firstNums; j < allUniques.size(); j++)
		{
			if(docFreq[j] > threshold)
			{
				if(!DFterms.contains(allUniques.get(j)))
				{
					DFterms.add(allUniques.get(j));
				}
			}
		}
		System.out.println("DFthreshold:" + threshold);
		System.out.println("DFterms:" + DFterms.size());
		
//		int count = 0;
//		for(int j = 0; j < allUniques.size(); j++)
//		{
//			if(docFreq[j] > threshold)
//			{
//				System.out.println(allUniques.get(j)+ ":" + docFreq[j]);
//				count++;
//			}
//		}
//		System.out.println(count);
						
		String[] DFtermsDic = new String[DFterms.size()];
		return (String[]) DFterms.toArray(DFtermsDic);
	}
	
	//往特征词字典中添加元素的方法
	private Object AddElement(Dictionary<Object, Object> collection, Object key, Object newValue)
	{
		Object element = collection.get(key);
		collection.put(key, newValue);
		return element;
	}
}

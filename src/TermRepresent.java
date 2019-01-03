
import java.util.*;

//此模块的功能是根据特征词典，将原始文本集进行特征表示，使用TF*IDF算法
public class TermRepresent {
	
	private static String[] docs;//读入的文本集
	private static String[] terms;//读入的特征词典
	private int numDocs;//总记录条数
	private int numTerms;//总特征词个数
	private int[][] termFreq;
	private float[][] termWeight;
	private int[] maxTermFreq;//记录各条记录中出现次数最多的特征词频率
	private int[] docFreq;
	private Dictionary wordsIndex = new Hashtable();//程序使用的特征词典
	private String[] trDocs;//文本集的特征表示
	
	//基于特征词典对原始文本集进行特征表示，返回String类型的数组
	public String[] TermRepresentMain(String[] allDocs, String[] termDic)
	{
		docs = allDocs;
		terms = termDic;
		numDocs = allDocs.length;//记录总条数
		numTerms = termDic.length;//特征向量维度
		maxTermFreq = new int[numDocs];
		docFreq = new int[numTerms];
		termFreq = new int[numTerms][];
		termWeight = new float[numTerms][];
		
		//把所有特征词加入特征词典wordsIndex中
		for(int i = 0; i < terms.length; i++)
		{
			termWeight[i] = new float[numDocs];
			termFreq[i] = new int[numDocs];
			AddElement(wordsIndex, terms[i], i);
		}
		
		//计算TF
		GenerateTermFrequency();
		//计算Weight
		GenerateTermWeight();
		
//		//将二维权值数组Weight转为KNN需要的格式
//		System.out.println(termWeight.length + ", " + termWeight[0].length);
//		String[] result = new String[docs.length];
//		for(int i = 0; i < termWeight[0].length; i++)
//		{
//			result[i] = docs[i].substring(0, 1);
//			for(int j = 0; j < termWeight.length; j++)
//			{
//				result[i] += " " + termWeight[j][i];
//			}
//		}
		
		//将二维权值数组Weight转为SVM需要的格式
		System.out.println("termDim:" + termWeight.length + ", recordLength:" + termWeight[0].length);
		String[] result = new String[docs.length];
		for(int i = 0; i < termWeight[0].length; i++)
		{
			result[i] = docs[i].substring(0, 1);
			for(int j = 0; j < termWeight.length; j++)
			{
				if(termWeight[j][i] != 0)
				{
					result[i] += " " + j + ":" + termWeight[j][i];
				}
			}
		}
		
		return result;
	}
	
	//往特征词字典中添加元素的方法
	private Object AddElement(Dictionary<Object, Object> collection, Object key, Object newValue)
	{
		Object element = collection.get(key);
		collection.put(key, newValue);
		return element;
	}
	
	//计算TF（特征词在各条记录中出现的次数）
	private void GenerateTermFrequency()
	{
		for(int i = 0;i < numDocs; i++)
		{
			String curDoc = docs[i];
			Dictionary freq = GetWordFrequency(curDoc);
			Enumeration enums = freq.keys();
			while(enums.hasMoreElements())
			{
				String word = (String)enums.nextElement();
				int wordFreq = (Integer)freq.get(word);
				int termIndex = GetTermIndex(word);
				if(termIndex == -1)//该特征词不在特征词典内
				{
					continue;
				}
//				System.out.println(word);
				termFreq[termIndex][i] = wordFreq;
				docFreq[termIndex]++;
				
				if(wordFreq > maxTermFreq[i])
				{
					maxTermFreq[i] = wordFreq;
				}
			}
//			System.out.println();
//			for(int j = 0; j < numTerms; j++)
//			{
//				System.out.print(termFreq[j][i]);
//			}
//			System.out.println();
		}
//		for(int i = 0; i < numDocs; i++)
//		{
//			System.out.println(maxTermFreq[i]);
//		}
	}
	private Dictionary GetWordFrequency(String input)//求该条记录各个特征词的频率
	{
		String convertedInput = input.toLowerCase();
		String[] words = convertedInput.split("([|])");
		Arrays.sort(words);
		
		String[] distinctWords = GetDistinctWords(words);//去除该条记录重复的特征词
		
		Dictionary result = new Hashtable();
		for(int i = 0; i < distinctWords.length; i++)
		{
			Object temp;
			temp = CountWords(distinctWords[i], words);
			result.put(distinctWords[i], temp);
		}
		return result;
	}
	private static String[] GetDistinctWords(String[] input)//去除该条记录重复的特征词
	{
		if(input == null)
		{
			return new String[0];
		}
		else
		{
			List<String> list = new ArrayList<String>();
			for(int i = 0; i < input.length; i++)
			{
				if(!list.contains(input[i]))
				{
					list.add(input[i]);
				}
			}
			String[] v = new String[list.size()];
			return (String[]) list.toArray(v);
		}
	}
	private int CountWords(String word, String[] words)//查找word元素在words数组中出现的次数
	{
		int itemIdx = Arrays.binarySearch(words, word);//如果word在words数组中，返回其索引
		if(itemIdx > 0)
		{
			while(itemIdx > 0 && words[itemIdx].equals(word))
			{
				itemIdx--;
			}
		}
		
		int count = 0;
		while(itemIdx < words.length && itemIdx >= 0)
		{
			if(words[itemIdx].equals(word))
			{
				count++;
			}
			itemIdx++;
			if(itemIdx < words.length)
			{
				if(!words[itemIdx].equals(word))
				{
					break;
				}
			}
		}
		return count;
	}
	private int GetTermIndex(String term)//返回特征词term在特征词典中的索引，若不在词典内返回-1
	{
		Object index = wordsIndex.get(term);
		if(index == null)
		{
			return -1;
		}
		else
		{
			return (Integer)index;
		}
	}
	
	//计算Weight
	private void GenerateTermWeight()
	{
		for(int i = 0; i < numTerms; i++)
		{
			for(int j = 0; j < numDocs; j++)
			{
				termWeight[i][j] = ComputeTermWeight(i, j);
			}
		}
	}
	private float ComputeTermWeight(int term, int doc)
	{
		float tf = GetTermFrequency(term, doc);
		float idf = GetInverseDocumentFrequence(term);
		//System.out.println("tf:" + tf + ", idf:" + idf);
		return tf * idf;
	}
	private float GetTermFrequency(int term, int doc)
	{
		int freq = termFreq[term][doc];
		int maxfreq = maxTermFreq[doc] + 1;//加一为了防止除0错误
		return ((float)freq / (float)(maxfreq));
	}
	private float GetInverseDocumentFrequence(int term)//文档频率的倒数
	{
		int df = docFreq[term];
		if(df == 0)
		{
			return 0;
		}
		else
		{
			return Log((float)(numDocs) / (float)df);
		}
	}
	private float Log(float num)
	{
		return (float) Math.log(num);
	}
}

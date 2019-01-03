import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//文档集合

public class Corpus {
	List<int[]> documentList;
	static Vocabulary vocabulary;
	static List<String> lableList;
	
	public Corpus()
	{
		documentList = new LinkedList<int[]>();
		vocabulary = new Vocabulary();
		lableList = new ArrayList<String>();
	}
	
	public int[] addDocument(List<String> document)
	{
		int[] doc = new int[document.size()];
		int i = 0;
		for(String word : document)
		{
			doc[i++] = vocabulary.getId(word, true);
		}
		documentList.add(doc);
		return doc;
	}
	
	public int[][] toArray()
	{
		return documentList.toArray(new int[0][]);
	}
	
	public int getVocabularySize()
	{
		return vocabulary.size();
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		for(int[] doc : documentList)
		{
			sb.append(Arrays.toString(doc)).append("\n");
		}
		sb.append(vocabulary);
		
		return sb.toString();
	}
	
	//加载文档集合
	//folderPath是存放文档的文件夹路径
	public static Corpus load(String folderPath) throws IOException
	{
		Corpus corpus = new Corpus();
		File folder = new File(folderPath);
		for(File file : folder.listFiles())
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
			String line;
			List<String> wordList = new LinkedList<String>();
			while((line = br.readLine()) != null)
			{
//				System.out.println(line);
				String[] words = line.split("([|])");
				lableList.add(words[0]);
				for(String word : words)
				{
//					System.out.println(word);
					if(word.trim().length() < 2)
					{
						continue;
					}
					wordList.add(word);
				}
			}
			br.close();
			corpus.addDocument(wordList);
		}
		if(corpus.getVocabularySize() == 0)
		{
			return null;
		}
		
		return corpus;
	}
	
	public Vocabulary getVocabulary()
	{
		return vocabulary;
	}
	
	public int[][] getDocument()
	{
		return toArray();
	}
	
	public String[] getLableList()
	{
		String[] lable = new String[lableList.size()];
		return (String[]) lableList.toArray(lable);
	}
	
	public static int[] loadDocument(String path, Vocabulary vocabulary) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		List<Integer> wordList = new LinkedList<Integer>();
		while((line = br.readLine()) != null)
		{
			String[] words = line.split("([|])");
			for(String word : words)
			{
				if(word.trim().length() < 2)
				{
					continue;
				}
				Integer id = vocabulary.getId(word);
				if(id != null)
				{
					wordList.add(id);
				}
			}
		}
		br.close();
		int[] result = new int[wordList.size()];
		int i = 0;
		for(Integer integer : wordList)
		{
			result[i++] = integer;
		}
		
		return result;
	}
}

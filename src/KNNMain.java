import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class KNNMain {
	//从数据文件中读取数据
	// datas 存储数据的集合对象
	// path 数据文件的路径
	public void read(List<List<Double>> datas, String path){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String data = br.readLine();
			List<Double> l = null;
			while (data != null) {
				String t[] = data.split(" ");
				l = new ArrayList<Double>();
				for (int i = 0; i < t.length; i++) {
					l.add(Double.parseDouble(t[i]));
				}
				datas.add(l);
				data = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//程序执行入口
	public static void main(String[] args) throws IOException{
		KNNMain t = new KNNMain();
		InputOutput rw=new InputOutput();
		
		//************************训练集的预处理，训练集放在Data目录下
	////子功能2：训练集的预处理（将训练集进行切词等预处理，形成原始词集合）
		String dir="10crossdata_eval_KNN/2/";
		String trainFile=dir+"InitTrainSet.txt";
		String CorpusFile=dir+"test/testSet.txt";//待分类文本集
		
		//InputOutput rw=new InputOutput();
		TextPreProcess p=new TextPreProcess(); 
		
		//1 从文件 trainFile读入训练集放在String[](its)中		
		String[] its=rw.readInput(trainFile);
		
		//2 对训练集预处理之后形成的训练集的词集合放在一个String[]（docs）中
		String[] docs=p.preProcessMain(its);
		
		//3 将放在String[]中的训练集写入文件
		String trainFileSeg=trainFile.substring(0,trainFile.lastIndexOf("."))+"Segment.txt";		
		rw.writeOutput(docs, trainFileSeg);
		System.out.println("预处理完毕");
		///////////////////////////////////////////////////////////////////////
		////子功能3：建立特征词典（使用合适的特征选择算法确定特征词）		
		
		//3.0使用局部DF
		DFTermSelect td=new DFTermSelect();
		
		//3.1使用IG		
		//IGTermSelect td=new IGTermSelect();
		
		//3.2使用CHI
		//CHITermSelect td=new CHITermSelect();
		
		//对训练集的词集合选择使用特征选择方法建立特征词典放在一个String[]（terms）中
		String[] terms=td.TermDictionaryMain(docs);		
		
		//将特征词典输出到文件,这个文件是训练阶段输出的第一个必需的文件
		String termDicFile=trainFile.substring(0,trainFile.lastIndexOf("/")+1)+"termDic.txt";
		rw.writeOutput(terms, termDicFile);
		
/////////////////////////////////////////////////////////////////////////////////
		//子功能4：训练集的特征表示（将训练集的词集合String[]（docs）基于特征词典String[]（terms）
		//进行文本表示放在一个String[]（trDocs）中）
		TermRepresent tr=new TermRepresent();
		
		//2基于特征词典对文本集进行特征表示放在一个String[](trDocs)中
		String[] trDocs=tr.TermRepresentMain(docs,terms);
				
		//3将训练集的特征表示写入到文件，这个文件的内容是svm要求的数据格式
		String trFile=trainFile.substring(0,trainFile.lastIndexOf("."))+"SegmentTR.txt";
		rw.writeOutput(trDocs, trFile);	
		
		//***************待分类文本集的预处理，放在Data/test目录下
		//1读入待分类文本集文件
		//String CorpusFile="Data/Test/testSet.txt";
		//InputOutput rw=new InputOutput();
		String[] inputCorpus=rw.readInput(CorpusFile);
		
		
		//InitTestSet its=new InitTestSet();
		//String[] newCorpus=its.InitTestSetMain(inputCorpus,200,1500,1700,1999);
		
		//2预处理
		//PreProcess p=new PreProcess();
		String[] docs1=p.preProcessMain(inputCorpus);
		String testFileSeg=CorpusFile.substring(0,CorpusFile.lastIndexOf("."))+"Segment.txt";		
		rw.writeOutput(docs1, testFileSeg);
		System.out.println("预处理完毕");
		//3文本表示
		//读入特征词典文件
		//String termDicFile="Data/termDic.txt";
		//String[] terms1=rw.readInput(termDicFile);
		
		//基于特征词典进行文本表示
		//TermRepresent tr=new TermRepresent();	
		String[] trDocs1=tr.TermRepresentMain(docs1,terms);
		
		//将文本表示写入文件
		String trFile1=CorpusFile.substring(0,CorpusFile.lastIndexOf("."))+"TR.txt";
		//String trFile1="Data/Test/testSet.TR";
		rw.writeOutput(trDocs1, trFile1);	
		 
		 //**********************数据准备完毕
		try {
			List<List<Double>> datas = new ArrayList<List<Double>>();
			List<List<Double>> testDatas = new ArrayList<List<Double>>();
			t.read(datas, trFile);			
			t.read(testDatas, trFile1);
			//t.read(datas, trainFile);			
			//t.read(testDatas, CorpusFile);
			KNN knn = new KNN();
			int correct=0;
			int error=0;
			String[] result=new String[testDatas.size()];
			for (int i = 0; i < testDatas.size(); i++) {
				List<Double> test = testDatas.get(i);
				//System.out.print("测试元组: ");
				for (int j = 1; j < test.size(); j++) {
					//System.out.print(test.get(j) + " ");
				}
				System.out.print("类别为: ");				
				//System.out.println(Math.round(Float.parseFloat((knn.knn(datas, test, 3)))));
				String label=knn.knn(datas, test, 3);//k = 3,可调整				
				System.out.println(label);
				result[i]=label;
				//统计正确率
				if(test.get(0).equals(Double.parseDouble(label)))
				{
					correct+=1;
				}
			}
			double total=testDatas.size();
			double rate=correct/total;
			System.out.println("正确率："+rate+"("+correct+"/"+testDatas.size()+")");
			//将结果写入文件
			
			//String resultFile=trFile1.substring(0,trFile1.lastIndexOf("."))+"result.txt";
			String resultFile=CorpusFile.substring(0,CorpusFile.lastIndexOf("."))+"result.txt";
			System.out.println(resultFile);
			rw.writeOutput(result, resultFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

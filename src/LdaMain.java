import java.io.IOException;
import java.util.Map;

public class LdaMain {
	
	public static void main(String[] args) throws IOException{
		String dir="10crossdata_eval/1/";
		//1.加载文档集合
		Corpus corpus = Corpus.load( dir+ "SelectLdaData"); 
		//2.创建一个LDA实例
		LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
		//3.训练
		ldaGibbsSampler.gibbs(50);//50个主题
		//4.得到LDA模型，使用LdaUtil可视化
		double[][] theta = ldaGibbsSampler.getTheta();
		System.out.println(theta.length + " " + theta[0].length);
		String[] lable = corpus.getLableList();
		String[] result = new String[lable.length];
		for(int i = 0;i < theta.length; i++)
		{
			result[i] = "" + lable[i];
			for(int j = 0; j < theta[0].length; j++)
			{
				if(theta[i][j] != 0)
				{
					result[i] += " " + j + ":" + theta[i][j];
				}
			}
//			System.out.println(result[i]);
		}
//		for(int i = 0; i < theta.length; i++)
//		{
//			for(int j = 0; j < theta[0].length; j++)
//			{
//				System.out.print(theta[i][j]);
//				System.out.print(" ");
//			}
//			System.out.println();
//		}
		double[][] phi = ldaGibbsSampler.getPhi();
//		System.out.println(phi.length + " " + phi[0].length);
//		for(int i = 0; i < phi.length; i++)
//		{
//			for(int j = 0; j < phi[0].length; j++)
//			{
//				System.out.print(phi[i][j]);
//				System.out.print(" ");
//			}
//			System.out.println();
//		}
		Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 50);
		LdaUtil.explain(topicMap);
		
		//训练
		//3将训练集的特征表示写入到文件，这个文件的内容是svm要求的数据格式
		String trainFile= dir + "/InitTrainSet.txt";
		InputOutput rw=new InputOutput();
		String trFile=trainFile.substring(0,trainFile.lastIndexOf("."))+"SegmentTR.txt";
		rw.writeOutput(result, trFile);
		
		/////////////////////////////////////////////////////////////////////////////////
		//子功能5：对训练集的特征表示进行归一化处理（[0，1]）
		//先基于训练集产生range文件，之后使用range文件对训练集进行归一化处理
		
		//1基于训练集产生range文件
		String rangeFile=trFile+".range";
		String argv[]={"-l","0","-s",rangeFile,trFile};
		SVMScale s = new SVMScale();
		s.run(argv);
		//2使用range文件对训练集进行归一化处理
		String scaleFile=trFile+".scale";
		String argv1[]={"-t",scaleFile,"-r",rangeFile,trFile};
		s.run(argv1);
		////////////////////////////////////////////////////////////////////////////////
		//子功能6：建立分类模型
		String modelFile=scaleFile+".model";
		String argv2[]={"-c","0.5","-t","0",scaleFile,modelFile};
		SVMTrain train = new SVMTrain();		
		train.run(argv2);
		
		//写入预测信息
		Corpus corpusTest = Corpus.load( dir + "/SelectLdaDataTest");
//		for(int[] doc : corpusTest.documentList)
//		{
//			double[] thetaTest = LdaGibbsSampler.inference(phi, doc);
//			for(int j = 0; j < thetaTest.length; j++)
//			{
//				System.out.print(thetaTest[j]);
//				System.out.print(" ");
//			}
//			System.out.println();
//		}
		String[] testLable = corpusTest.getLableList();
		String[] testResult = new String[corpusTest.documentList.size()];
		for(int i = 0; i < corpusTest.documentList.size(); i++)
		{
			int[] doc = corpusTest.documentList.get(i);
			double[] thetaTest = LdaGibbsSampler.inference(phi, doc);
			testResult[i] = "" + testLable[i];
			for(int j = 0; j < thetaTest.length; j++)
			{
				testResult[i] += " " + j + ":" + thetaTest[j];
			}
//					System.out.println(testResult[i]);
		}
		
		//将文本表示写入文件
		String trFileTest=dir+"Test/testSet.TR";
		rw.writeOutput(testResult, trFileTest);
	}
}

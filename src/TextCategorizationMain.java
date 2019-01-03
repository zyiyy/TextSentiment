
import java.io.IOException;


public class TextCategorizationMain  {
	public static void main(String[] args) throws IOException{
	
	//1读入待分类文本集文件
	String dir="10crossdata_eval/10/";
	String CorpusFile=dir+"Test/testSet.txt";
	InputOutput rw=new InputOutput();
	String[] inputCorpus=rw.readInput(CorpusFile);
	
	
	//InitTestSet its=new InitTestSet();
	//String[] newCorpus=its.InitTestSetMain(inputCorpus,200,1500,1700,1999);
	
	//2预处理
	TextPreProcess p=new TextPreProcess();
	String[] docs=p.preProcessMain(inputCorpus);
	String trainFileSeg=CorpusFile.substring(0,CorpusFile.lastIndexOf("."))+"Segment.txt";		
	rw.writeOutput(docs, trainFileSeg);
	System.out.println("预处理完毕");
	//3文本表示
	//读入特征词典文件
	String termDicFile=dir+"termDic.txt";
	String[] terms=rw.readInput(termDicFile);
	
	//特征表示
	String termEasyRepresentFile=CorpusFile.substring(0,CorpusFile.lastIndexOf("."))+"termEasyRepresent.txt";
	TermEasyRepresent ter = new TermEasyRepresent();
	String[] termEasyRepresent = ter.TermEasyRepresentMain(docs, terms);
	rw.writeOutput(termEasyRepresent, termEasyRepresentFile);
	
	//基于特征词典进行文本表示
	TermRepresent tr=new TermRepresent();	
	String[] trDocs=tr.TermRepresentMain(docs,terms);
	
	//将文本表示写入文件
	String trFile=dir+"Test/testSet.TR";
	rw.writeOutput(trDocs, trFile);
	
	
	//4scale处理
	//2使用range文件对训练集进行归一化处理
	String scaleFile=trFile+".scale";
	String rangeFile=dir+"InitTrainSetSegmentTR.txt.range";
	String argv[]={"-t",scaleFile,"-r",rangeFile,trFile};
	SVMScale s = new SVMScale();
	s.run(argv);
	
	
	//5 文本分类
	//三个参数分别是经过Scale处理的待分类的数据文件，存放分类器模型的文件，存储分类结果的文件
	//String scaleFile="10crossdatairis_libsvm_scale/3/testSet.txt";
	//String modelFile="10crossdatairis_libsvm_scale/3/InitTrainSet.txt.model";
	
	String modelFile=dir+"InitTrainSetSegmentTR.txt.scale.model";
	String predictFile=scaleFile+".predict";
	String argv1[]={scaleFile,modelFile,predictFile};
	
	//String modelFile="Data/InitTrainSetSegmentTR.txt.model";
	//String predictFile=trFile+".predict";
	//String argv1[]={trFile,modelFile,predictFile};
	SVMPredict predict = new SVMPredict();		
	predict.run(argv1);
	
	
}
	
}

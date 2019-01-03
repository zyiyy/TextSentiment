import java.io.IOException;

public class LdaPredict {
	public static void main(String[] args) throws IOException{
		String dir="10crossdata_eval/1/";
		//将文本表示写入文件
		String trFileTest=dir+"Test/testSet.TR";
		
		//4scale处理
		//2使用range文件对训练集进行归一化处理
		String scaleFileTest=trFileTest+".scale";
		String rangeFileTest=dir+"InitTrainSetSegmentTR.txt.range";
		String argvTest[]={"-t",scaleFileTest,"-r",rangeFileTest,trFileTest};
		SVMScale sTest = new SVMScale();
		sTest.run(argvTest);
		
		
		//5 文本分类
		//三个参数分别是经过Scale处理的待分类的数据文件，存放分类器模型的文件，存储分类结果的文件
		//String scaleFile="10crossdatairis_libsvm_scale/3/testSet.txt";
		//String modelFile="10crossdatairis_libsvm_scale/3/InitTrainSet.txt.model";
		
		String modelFileTest=dir+"InitTrainSetSegmentTR.txt.scale.model";
		String predictFileTest=scaleFileTest+".predict";
		String argv1Test[]={scaleFileTest,modelFileTest,predictFileTest};
		
		//String modelFile="Data/InitTrainSetSegmentTR.txt.model";
		//String predictFile=trFile+".predict";
		//String argv1[]={trFile,modelFile,predictFile};
		SVMPredict predict = new SVMPredict();		
		predict.run(argv1Test);
	}
}

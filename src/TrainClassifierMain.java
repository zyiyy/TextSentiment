

import java.io.IOException;





public class TrainClassifierMain {
	public static void main(String[] args) throws IOException{
		///////////////////////////////////////////////////////////////////////
		/*
		////子功能1：建立训练集（从初始的训练集中按比例选取一定数量文本建立最终的训练集）		
		InitTrainSet t=new InitTrainSet(); 
		
		
		//1最终训练集文件的路径
		String trainFile="Data/InitTrainSet.txt";
		int Samples=400;//用户设定的训练集样本总数		
		
		//2 从原始文本集抽取出一定数量的样本形成的训练集放在另一个String[](ts)中
		String[] ts=t.InitTrainSetMain(Samples);
		
		//3 将放在String[](ts)中的训练集写入文件 trainFile
		InputOutput rw=new InputOutput();
		rw.writeOutput(ts, trainFile);
		*/
		///////////////////////////////////////////////////////////////////////
		////子功能2：训练集的预处理（将最终的训练集进行切词等预处理，形成原始词集合）
		String trainFile="10crossdata_eval/10/InitTrainSet.txt";
		InputOutput rw=new InputOutput();
		TextPreProcess p=new TextPreProcess(); 
		
		//1 从文件 trainFile读入训练集放在String[](its)中		
		String[] its=rw.readInput(trainFile);
		
		//2 对训练集预处理之后形成的训练集的词集合放在一个String[]（docs）中
		String[] docs=p.preProcessMain(its);
		
		//3 将放在String[]中的训练集写入文件，这个文件可以不用产生,后续可以考虑去掉这次IO
		String trainFileSeg=trainFile.substring(0,trainFile.lastIndexOf("."))+"Segment.txt";		
		rw.writeOutput(docs, trainFileSeg);
		
		///////////////////////////////////////////////////////////////////////
		////子功能3：建立特征词典（首先将训练集的词集合去重，之后使用合适的特征选择算法进一步确定特征词）
		//TermDictionary td=new TermDictionary(); 
		
		//3.1使用全局DF
		//DFTermSelect td=new DFTermSelect();
		
		//3.2使用局部DF
		DFTermSelect td=new DFTermSelect();
		
		//3.3使用IG
		//IGTermSelect td=new IGTermSelect();
				
		//3.4使用CHI
		//CHITermSelect td=new CHITermSelect();
		
		//1对训练集的词集合选择使用特征选择方法建立特征词典放在一个String[]（terms）中
		String[] terms=td.TermDictionaryMain(docs);		
		
		//2将特征词典输出到文件,这个文件是训练阶段输出的第一个必需的文件
		String termDicFile=trainFile.substring(0,trainFile.lastIndexOf("/")+1)+"termDic.txt";
		rw.writeOutput(terms, termDicFile);
		
		//特征表示
		String termEasyRepresentFile=trainFile.substring(0,trainFile.lastIndexOf("/")+1)+"termEasyRepresent.txt";
		TermEasyRepresent ter = new TermEasyRepresent();
		String[] termEasyRepresent = ter.TermEasyRepresentMain(docs, terms);
		rw.writeOutput(termEasyRepresent, termEasyRepresentFile);
		
		/////////////////////////////////////////////////////////////////////////////////
		//子功能4：训练集的特征表示（将训练集的词集合String[]（docs）基于特征词典String[]（terms）
		//进行文本表示放在一个String[]（trDocs）中）
		TermRepresent tr=new TermRepresent();
		
		//2基于特征词典对文本集进行特征表示放在一个String[](trDocs)中
		String[] trDocs=tr.TermRepresentMain(docs,terms);
				
		//3将训练集的特征表示写入到文件，这个文件的内容是svm要求的数据格式
		String trFile=trainFile.substring(0,trainFile.lastIndexOf("."))+"SegmentTR.txt";
		rw.writeOutput(trDocs, trFile);
		
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
	}
}

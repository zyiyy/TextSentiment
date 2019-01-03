import java.util.*;



public class CHITermSelect {
	//利用卡方统计量进行特征选择
	
	int numOfClass;//训练集中包括的类别个数。本题中为3
	
	public CHITermSelect()	{ numOfClass=3;	}
	
	public CHITermSelect(int num)
	{ numOfClass=num;	}
	

	 public String[] TermDictionaryMain(String[] docs)
	 {
		 int numOfDocs=docs.length;//包含的文档个数
		 LinkedList<String> allTerm=new LinkedList<String>();//存放所有文档中包括的所有单词
		 for(int i=0;i<numOfDocs;i++)//构建包含所有单词的单词表
		 {
			 String d=docs[i];
			 
			 if (d.length()>2)
			 d=d.substring(2);
			 else
				 continue;
			 String[] terms=d.split("\\|");
			 LinkedList<String> termlist=new LinkedList<String>();//存放当前文档所包含的单词
			 for(int j=0;j<terms.length;j++)
			 {
				 if(!termlist.contains(terms[j]))
				 {
					 termlist.add(terms[j]);
				 }
			 }
			 for(int j=0;j<termlist.size();j++)
			 {
				 if(!allTerm.contains(termlist.get(j)))
				 {
					 allTerm.add(termlist.get(j));
				 }
			 }
			 
		 }
		 
		 
		 int numOfTerm=allTerm.size();
		 System.out.println("词的总个数："+numOfTerm);
		 int[][]weights=new int[numOfTerm][numOfClass*2];//记录每个单词在每个类中出现的次数，行序号对应allTerm中对应位置的单词
		                                        //第0列表示单词在类0中出现的次数，第1列表示单词在类1中出现的次数，第2列表示单词在类2中出现，第3列表示单词在类0中不出现，第4列表示单词在类1中不出现，第5列表示单词在类2中不出现，
		 for(int i=0;i<numOfTerm;i++)
			 for(int j=0;j<numOfClass*2;j++)
				 weights[i][j]=0;
		 
		 for(int i=0;i<numOfDocs;i++)//统计每个单词在每个类中出现的次数
		 {
			 String d=docs[i];
			 int classNo=Integer.parseInt(d.substring(0, 1));
			 if (d.length()>2)
				 d=d.substring(2);
				 else
					 continue;
			 String[] terms=d.split("\\|");
			 LinkedList<String> termlist=new LinkedList<String>();//存放当前文档所包含的单词
			 for(int j=0;j<terms.length;j++)
			 {
				 if(!termlist.contains(terms[j]))
				 {
					 termlist.add(terms[j]);
				 }
			 }
			 for(int j=0;j<allTerm.size();j++)
				 weights[j][numOfClass+classNo]++;
			 
			 for(int j=0;j<termlist.size();j++)
			 {
				 int tempindex=allTerm.indexOf(termlist.get(j));
				 if(tempindex>=0)
				 {
					 weights[tempindex][numOfClass+classNo]--;
					 weights[tempindex][classNo]++;
				 }
			 }
		 }
		 //计算卡方值
		 double[] finalweight=new double[numOfTerm];
		 for(int i=0;i<numOfTerm;i++)
		 {
			 finalweight[i]=0;
			 int[] tt=weights[i];
			 for (int j=0;j<numOfClass;j++)
			 {
				 double temp=(1.0*(tt[j]+tt[j+numOfClass])/docs.length)*((tt[0]+tt[1]+tt[2]));
				 finalweight[i]+=(tt[j]-temp)*(tt[j]-temp)/temp;
				 temp=(1.0*(tt[j]+tt[j+numOfClass])/docs.length)*((tt[3]+tt[4]+tt[5]));
				 finalweight[i]+=(tt[j+numOfClass]-temp)*(tt[j+numOfClass]-temp)/temp;
			 }
		 }
		
		 class MyType{
			 String data;
			 double weight;
			 MyType(String d, double w)
			 {
				 data=d;
				 weight=w;
			 }
		 }
		 Comparator<MyType> myComparator=new Comparator<MyType>(){
			 public int compare(MyType a,MyType b)
			 {
				 if(a.weight-b.weight<0)
					 return -1;
				 if(a.weight-b.weight>0)
				 return 1;
				 return 0;
			 }
		 };
		 //对单词按卡方统计量进行排序
		 LinkedList<MyType> tmpList=new LinkedList<MyType>();
		 for(int i=0;i<numOfTerm;i++)
		 {
			 MyType my=new MyType(allTerm.get(i),finalweight[i]);
			 tmpList.add(my);
		 }
		 //Collections.sort(tmpList,myComparator);//该函数按升序排列
		 Collections.sort(tmpList,Collections.reverseOrder(myComparator));
		 
		 //假设取所有单词的30%作为特征进行选取
		 
		 //int tmpNum=(numOfTerm*3/10);
		 int tmpNum=(int) (numOfTerm*0.3);
		 System.out.println("总词数："+tmpNum);
		 String[] result=new String[tmpNum];
		 for(int i=0;i<tmpNum;i++)
			 result[i]=tmpList.get(i).data;
		 return result;
	 }
	 
	

}

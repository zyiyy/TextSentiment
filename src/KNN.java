
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

//KNN算法主体类
public class KNN {
	//设置优先级队列的比较函数，距离越大，优先级越高
	private Comparator<KNNNode> comparator = new Comparator<KNNNode>() {
		public int compare(KNNNode o1, KNNNode o2) {
			if (o1.getDistance() >= o2.getDistance()) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	//获取K个不同的随机数
	// k 随机数的个数
	// max 随机数最大的范围
	// 返回 生成的随机数数组
	public List<Integer> getRandKNum(int k, int max) {
		List<Integer> rand = new ArrayList<Integer>(k);
		for (int i = 0; i < k; i++) {
			int temp = (int) (Math.random() * max);
			if (!rand.contains(temp)) {
				rand.add(temp);
			} else {
				i--;
			}
		}
		return rand;
	}
	//计算测试元组与训练元组之前的距离
	// d1 测试元组
	// d2 训练元组
	//返回 距离值
	public double calDistance(List<Double> d1, List<Double> d2) {
		/*
		//计算两个非稀疏向量的距离
		double distance = 0.00;
		int maxlength;
		int minlength;
		if (d2.size()>d1.size()) 
		{
			maxlength=d2.size();
			minlength=d1.size();
		}
		else
		{
			maxlength=d1.size();
			minlength=d2.size();
		}
		
		for (int i = 1; i <minlength; i++) {
			distance += (d1.get(i) - d2.get(i)) * (d1.get(i) - d2.get(i));
		}
		for (int i = minlength; i <maxlength; i++) {
			if(d1.size()==minlength)
			distance += (0 - d2.get(i)) * (0 - d2.get(i));
			else
				distance += (d1.get(i) - 0) * (d1.get(i) - 0);	
		}
		return distance;
		*/
		//计算两个向量的距离
		double distance = 0.00;
		for (int i = 1; i <d1.size(); i++) {
			distance += (d1.get(i) - d2.get(i)) * (d1.get(i) - d2.get(i));
		}
		return distance;
		
	}
	//执行KNN算法，获取测试元组的类别
	// datas 训练数据集
	// testData 测试元组
	// k 设定的K值
	//返回 测试元组的类别
	public String knn(List<List<Double>> datas, List<Double> testData, int k) {
		PriorityQueue<KNNNode> pq = new PriorityQueue<KNNNode>(k, comparator);
		List<Integer> randNum = getRandKNum(k, datas.size());
		//System.out.println("training "+datas.size());
		for (int i = 0; i < k; i++) {
			int index = randNum.get(i);
			List<Double> currData = datas.get(index);
			String c = currData.get(0).toString();
			KNNNode node = new KNNNode(index, calDistance(testData, currData), c);
			pq.add(node);
		}
		for (int i = 0; i < datas.size(); i++) {
			List<Double> t = datas.get(i);
			double distance = calDistance(testData, t);
			KNNNode top = pq.peek();
			if (top.getDistance() > distance) {
				pq.remove();
				pq.add(new KNNNode(i, distance, t.get(0).toString()));
			}
		}
		
		return getMostClass(pq);
	}
	//获取所得到的k个最近邻元组的多数类
        // pq 存储k个最近近邻元组的优先级队列
	//返回 多数类的名称/
	private String getMostClass(PriorityQueue<KNNNode> pq) {
		Map<String, Integer> classCount = new HashMap<String, Integer>();
		for (int i = 0; i < pq.size(); i++) {
			KNNNode node = pq.remove();
			String c = node.getC();
			if (classCount.containsKey(c)) {
				classCount.put(c, classCount.get(c) + 1);
			} else {
				classCount.put(c, 1);
			}
		}
		int maxIndex = -1;
		int maxCount = 0;
		Object[] classes = classCount.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			if (classCount.get(classes[i]) > maxCount) {
				maxIndex = i;
				maxCount = classCount.get(classes[i]);
			}
		}
		return classes[maxIndex].toString();
	}
}
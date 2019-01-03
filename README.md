# TextSentiment
文本(股吧评论)情感分析
这个项目数据集是从股吧爬下来的评论，自己进行标注。
1、中文分词使用IKAnalyzer包，SVM使用libSVM库。
2、Baseline：KNN
3、特征表示：
   1.TF × IDF
   2.LDA主题模型

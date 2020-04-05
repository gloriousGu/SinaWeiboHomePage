# SinaWeiboHomePage

仿新浪微博个人主页，NestedScrolling机制实现嵌套滚动，实现顶部拉伸刷新效果。

## 效果图

## 思路  

垂直滚动嵌套垂直滚动  

向下滚动时优先级：  

```
1.nestedChild向下滚动  

2.parent向下滚动  

3.拉伸背景layout。
```

向上滚动时优先级：  

```
1.背景layout拉伸还原  

2.parent向上滚动  

3.nestedChild向上滚动
```

由于有多个nestedChild放在一个horizontalScrollView中，还需要处理horizontalScrollView水平滚动和parent垂直滚动冲突问题。


## 草图  

<img src="./pic/sina微博个人主页设计草图-1.jpg"width="50%" height="50%"/>  

<img src="./pic/sina微博个人主页设计草图-2.jpg" width="50%" height="50%"/> 

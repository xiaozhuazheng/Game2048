## Game2048
通过自定义Layout,以及layout里存放的ItemView，并监听事件，实现2048小游戏。

#### 效果图

![avatar](/ShootScreen/shoot.png)

#### 实现思路

1. 首先，将游戏的所有格子画出来。这里，定义N，表示N行N列，即N*N个格子可以移动。每一个方块为一个自定义的GameItem。方块的长宽由layout决定。 
2. 自定义Layout,用于绘制所有方块，以及相应滑动监听。这是最主要的一部分，涉及到具体的算法。
3. 上面的两个步骤实质上定义了view，当然需要主程序跑起来啰。设置游戏结束以及得分的监听接口。

具体参考我的博客：https://blog.csdn.net/Mr_azheng/article/details/78897291

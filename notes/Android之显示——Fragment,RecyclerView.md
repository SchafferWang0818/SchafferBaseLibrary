---

title: Android之 Fragment & RecyclerView
categories: "android 总结"
tags: 
	- Fragment 
	- RecyclerView

---

## Fragment ##
- 为防止出现`has parent view`的情况,在check事件中`frameGroup.removeAllViews();`

### FragmentStatePagerAdapter 与 FragmentPagerAdapter ###
- 相同点: 都继承自`PagerAdapter`,` instantiateItem()`和`setPrimaryItem()`中均调用了`Fragment # setUserVisibleHint(boolean)`设置是否可见,用于懒加载
- 不同点:
	- `FragmentStatePagerAdapter`中全局设置`SavedState`列表,用于保存所有`Fragment`的状态,在` instantiateItem()`和`destroyItem()`均有状态更新,而后者没有;
	- 前者不论Fragment存不存在均调用`FragmentTransaction#add/remove()`,<br>后者判断Fragment存在时调用`FragmentTransaction#attach()`,不存在时调用`FragmentTransaction#detach()`



---
## RecyclerView ##



---
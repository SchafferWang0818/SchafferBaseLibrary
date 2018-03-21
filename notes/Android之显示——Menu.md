---

title: Android之 Menu
categories: "android 总结"
tags: 
	- OptionsMenu
	- SubMenu
	- PopupMenu
	- PopupWindow
	- ContextMenu 
---
# Menu

	分类:
		- OptionsMenu
		- SubMenu
		- PopupMenu & PopupWindow
		- ContextMenu 


---

<font color = "red">
	**注意:**
	<font color = "Blue">
			1. **`OptionsMenu`,`ContextMenu`均可以设置`SubMenu`;**
			2. **`ContextMenu`,`PopupMenu` 围绕`View`进行;**
			3. **`ContextMenu`需要注册和解注册;**
	
	</font>
</font>

---
## OptionsMenu(物理菜单按钮触发的菜单)
1. 旧版本(4.4之前)中最多呈现的item数量为6,其他item为more后;新版本中显示为一个list(即ActionBar);
2. 创建的方法为onCreateOptionsMenu(),可以用Java代码直接添加item,也可以填充menu.xml文件来完成.
3. item选中的动作设置需要调用onOptionsItemSelected()完成.
4. 实现步骤:
					
		1. onCreateOptionsMenu()中

				menu.add(Menu.NONE, 1, Menu.NONE, "设置1").setIcon(R.drawable.ic_launcher);
    			menu.add(Menu.NONE, 2, Menu.NONE, "设置2");
    			menu.add(Menu.NONE, 3, Menu.NONE, "设置3");
    			//参数一：groupId  组id  int 类型  表示添加的item可以分组进行管理。如果不需要分组可以不使用
    			//参数二： itemId  每一个item的id值。 int类型。 非常重要。 用来找到对应的item
    			//参数三：item的排序。 int类型。如果使用none表示按照默认排序
    			//参数四：item上显示的内容

		2. onOptionsItemSelected()中使用Switch分支结构对menu的点击情况进行判断.


---			    	
## SubMenu(其他菜单子菜单)
1. 在其他menu的某个选项的基础上设置子菜单,不支持图标设置,且子菜单不能二级嵌套,否则直接报告异常.
2. 以OptionsMenu为父菜单的实现步骤:
	1. onCreateOptionsMenu()中,在菜单选项4中,设置子菜单5,6,7,8;
			menu.add(Menu.NONE, 1, Menu.NONE, "设置1").setIcon(R.drawable.ic_launcher);
    		menu.add(Menu.NONE, 2, Menu.NONE, "设置2");
    		menu.add(Menu.NONE, 3, Menu.NONE, "设置3");
				
    		SubMenu subMenu = menu.addSubMenu(Menu.NONE, 4, Menu.NONE, "哈哈");
    		subMenu.setHeaderIcon(R.drawable.ic_launcher);
    		subMenu.setHeaderTitle("显示标题");//设置子菜单的标题
    		subMenu.add(Menu.NONE, 5, Menu.NONE, "设置5").setIcon(R.drawable.ic_launcher);
    		subMenu.add(Menu.NONE, 6, Menu.NONE, "设置6");
    		subMenu.add(Menu.NONE, 7, Menu.NONE, "设置8");
    		subMenu.add(Menu.NONE, 8, Menu.NONE, "设置9");
	2. 子菜单的点击事件也是在父菜单的on*****ItemSelected()中分支结构进行.

---
## ContextMenu(绑定控件或者布局的长按显示菜单)
1. 该菜单是绑定在view上,长按view弹出的菜单,没有个数限制,没有图标显示.
2. 实现步骤:

	1. 注册绑定view ->`registerForContextMenu(View)`;
	2. onCreateContextMenu()方法中设置menu菜单项,可以设置子菜单;
	3. onContextItemSelected()方法中switch分支结构判断响应事件;
	4. onDestroy()中解除注册绑定 ->`unregisterForContextMenu(View)`.

---
## PopupMenu和PopupWindow
1. 二者的区别:
	1. popPupMenu还是一个Menu菜单的呈现，popupWindow而是一个window窗口的呈现。
	2. popupMenu的呈现是围绕着控件呈现在控件的边缘。popupWindow即可以呈现在控件的周围页可以呈现在指定的location。
2. PopupWindow
	1. popupWindow在现实的时候正常显示，但是如果需要让当前的窗口响应了点击之后消失掉，就需要设置setBackgroundDrawable。
	2. 实现步骤:
		1. 在某个点击事件中:
			1. 创建对象
					PopupWindow popWindow  = new PopupWindow(this);
			2. 指定显示的宽高
				
					popWindow.setWidth(200);
	    			popWindow.setHeight(100);

			3. 关联显示的内容xml
				
					    View view = View.inflate(this, R.layout.view_popupwindow, null);
    					popWindow.setContentView(view);

			4. 设置window的显示位置

					    //popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_launcher));
    					//呈现在某个控件的下方
    					//popWindow.showAsDropDown(mytv2);
    					//popWindow.showAsDropDown(tv, 100, 100);
    					//呈现在具体的位置
    					popWindow.showAtLocation(tv, Gravity.CENTER, 100, 200);
			5. 给xml的view设置点击事件

					    TextView hahaTv = (TextView) view.findViewById(R.id.haha);
    					hahaTv.setOnClickListener(new View.OnClickListener() {
    						
    						@Override
    						public void onClick(View v) {
    							Log.i("TAg", "点击了");
    							
    							popWindow.dismiss();
    						}
    					});

	3. PopupMenu
		1. 呈现一个可移动的pop菜单，呈现在对应关联的view视图的周围。默认呈现在下方，如果下方不存在空间会above。
		2. popmenu在呈现之前需要当前的activity首先运行起来。否则拿不到当前的窗口令牌。
		3. 实现步骤:
			1. 点击事件中
				1. 初始化
					`PopupMenu popupMenu = new PopupMenu(this, tv);`
				2. 获取menu

					    Menu menu = popupMenu.getMenu();
    					menu.add(Menu.NONE, 1, menu.NONE, "哈哈");
    					menu.add(Menu.NONE, 2, menu.NONE, "呵呵");
    					menu.add(Menu.NONE, 3, menu.NONE, "嘿嘿");
				3. 呈现

					`popupMenu.show();`
				4. switch设置菜单项目的点击事件
					
5. 注:onCreate****Menu()中均可以通过填充menu.xml来完成设置菜单内容

	`getMenuInflater().inflate(R.menu.main, menu);`
	

----------
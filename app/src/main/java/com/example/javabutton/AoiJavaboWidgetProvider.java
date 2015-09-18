package com.example.javabutton;

/**
 * Created by davy on 15/09/18.
 */
public class AoiJavaboWidgetProvider extends JavaButtonWidgetProvider {
	@Override
	protected String getJavaActionName() { return "com.example.javabutton.javabuttonwidget.AOI_ACTION"; }
	@Override
	protected int getJavaboLayoutId() { return R.layout.aoi_appwidget; }
	@Override
	protected int getJavaBotanId() { return R.id.aoiButton; }
}

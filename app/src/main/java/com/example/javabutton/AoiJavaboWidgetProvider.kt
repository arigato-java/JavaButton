package com.example.javabutton

class AoiJavaboWidgetProvider : JavaButtonWidgetProvider() {
    protected override val javaActionName: String
        get() =  "com.example.javabutton.javabuttonwidget.AOI_ACTION"

    protected override val javaboLayoutId: Int
        get() = R.layout.aoi_appwidget

    protected override val javaBotanId: Int
        get() = R.id.aoiButton
}

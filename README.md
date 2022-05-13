# Widget

+ 위젯 만드는 방법 flow 
  - com.example.[프로젝트이름] 우클릭 이후, 새로 Java Class 만들기. 
  - 이름에 AppWidgetProvider 집어넣기. ex) ExampleAppWidgetProvider

<pre><code>
package com.example.exercise;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ExampleAppWIdgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // 앱위젯에서 처음에 업데이트 되는 것이나, 정기적으로 업데이트 될 때 호출되는 메소드이다.
        //
        for(int appWidgetID : appWidgetIds){
            Intent intent = new Intent(context,MainActivity.class); // Intent. 수행할 때 사용하는
            // 애라고 생각하면 편할듯.
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
            // PendingIntent 란, 가지고 있는 "Intent"를 당장 수행하지는 않고, 특정 시점에 수행하도록 하는 특징이 있다.
            // 특정 시점은, 보통 해당 앱이 구동되고 있지 않을 때이다. 주로 다른 것을 실행하고 있을 때, 푸시알림으로 Intent
            // 작업 수행하고 있을 때나, 바탕화면(위젯)에서 Intent 작업을 수행시 사용한다.

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_widget);
            // Appwidget에서 GUI를 그릴 때 사용하는 RemoteViews 객체이다.
            // AppWidget은 실제 어플리케이션과 서로 다른 프로세스에서 동작하는 만큼, 직접적으로 화면에 그림을 그릴 수 없다고 한다.
            // 자신이 원하는 형태로 화면을 그리도록, AppWidgetService를 통해, AppWidgetHost에게 부탁하는 형식으로 들어간다.
            // 이때 사용되는 클래스가 RemoteViews 이다.
            // RemoteView는, View를 만들기 위한 설계도라고 이해하면 쉽다고 한다.
            // 화면을 갱신하고자 할 때 반복적으로 RemoteView를 업데이트하며 이루어진다. 매번 새롭게 설계도를 작성한 후 해당 설계도가
            // 통째로 넘어가는 식이다. 비록 설계도는 새롭게 넘겨지더라도, 해당 설계도를 그리는 데 사용한 Layout이 동일한 이상, AppWidgetHost
            // 쪽에서 자신이 한번 그려 둔 View를 재활용하는 식이기 때문에, 매번 새롭게 View가 그려지지는 않는다.



        }
    }
}
  
</code></pre>  

+ https://developer.android.com/guide/topics/appwidgets?hl=ko 
  - 안드로이드 개발자 사이트 정보 정리
  - 필요한 것 : 
    - AppWidgetProviderInfo 객체
    - AppWidgetProvider 클래스 구현

+ https://darksilber.tistory.com/14?category=372671
  - 안드로이드 위젯 개발 관련 블로그.
    
+ https://lktprogrammer.tistory.com/132
  - 리니어 레이아웃과 버튼

+ https://developer.android.com/reference/android/os/BatteryManager?hl=ko
  - 배터리관련 클래스 및 메소드
  - https://abyser.tistory.com/87
  - http://daplus.net/java-android%EC%97%90%EC%84%9C-%EB%B0%B0%ED%84%B0%EB%A6%AC-%EC%9E%94%EB%9F%89-%EB%B0%8F-%EC%83%81%ED%83%9C-%ED%99%95%EC%9D%B8/
 
+ https://iw90.tistory.com/170
  -실제로 배터리위젯 만든 사람의 예시
+ https://mrcle.tistory.com/3
  - 디자인 및 레이아웃 템플릿 오픈소스
+ https://travel-nomad.tistory.com/25
  - sms 

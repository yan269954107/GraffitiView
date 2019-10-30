package com.yanxw.graffiti.newversion;

/**
 * AnnotationInterface
 * Created by yanxinwei on 2019-06-28.
 */
public interface AnnotationInterface {

    int getCurrentStatus();

    //dir 1:left  2:right
    void onFling(int direct);

}

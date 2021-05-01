package com.tuky.diploma.structures.graph;

public interface Moore2D {

    //region    VECTORS
    int NEIGHBOURS_COUNT  = 8;

    int VEC_TOP_LEFT       = 0;
    int VEC_TOP            = 1;
    int VEC_TOP_RIGHT      = 2;
    int VEC_LEFT           = 3;
    int VEC_RIGHT          = 4;
    int VEC_BOTTOM_LEFT    = 5;
    int VEC_BOTTOM         = 6;
    int VEC_BOTTOM_RIGHT   = 7;

    static int vecReverse(int vec) {
        return NEIGHBOURS_COUNT - vec - 1;
    }

}

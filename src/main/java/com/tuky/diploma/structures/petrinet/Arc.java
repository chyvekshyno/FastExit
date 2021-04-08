package com.tuky.diploma.structures.petrinet;

public class Arc {

    enum Direction {
        PLACE_TO_TRANSITION {
            @Override
            public boolean executable(Place p, int color) {
                return !p.isEmpty() && p.contains(color);
            }

            @Override
            public void exec(Place p, int color) {
                p.remove(color);
            }
        },

        TRANSITION_TO_PLACE {
            @Override
            public boolean executable(Place p, int color) {
                return true;
            }

            @Override
            public void exec(Place p, int color) {
                p.append(color);
            }
        };

        public abstract boolean executable(Place p, int color);
        public abstract void exec(Place p, int color);
    }

    //region    Fields
    private Place place;
    private Transition transition;
    private Direction direction;
    private int tr_color;
    //endregion


    //region    Constructors
    public Arc(Place place, Transition transition, Direction direction) {
        this(place, transition, direction, 0);
    }

    public Arc(Place place, Transition transition,  Direction direction, int tr_color) {
        this.place = place;
        this.transition = transition;
        this.direction = direction;
        this.tr_color = tr_color;
    }
    //endregion


    //region    Methods
    public boolean executable() {
        return direction.executable(place, tr_color);
    }
    public void exec() {
        direction.exec(place, tr_color);
    }

    //endregion
}

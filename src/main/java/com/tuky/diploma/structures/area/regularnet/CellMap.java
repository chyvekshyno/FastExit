package com.tuky.diploma.structures.area.regularnet;

/**
 * Create cell map from JSON file.
 * JSON-file describes building structure
 * Each object describes one floor (can't be repeated, may be as key)
 * Object has attributes:
 *      1.  floor
 *      2.  name    [optional]
 *      3.  door    (as the start point of room as wall array)
 *          wall s also an object:
 *              1.  length  (in cm/inch)
 *              2.  door    (distance from start of wall in cm/inch, width)    [optional]
 *                      is a new  room so is an array
 *                      if door is an empty array, it's the exit of the floor
 *              3.  vector  (of rotation from previous)     {l, r}
 *              4.  degree  [90 grad as default]
 *
 * Start build floor from input door
 */
public class CellMap {

}

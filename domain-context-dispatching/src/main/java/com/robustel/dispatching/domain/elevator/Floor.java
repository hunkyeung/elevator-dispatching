package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@Getter
@EqualsAndHashCode
public class Floor implements ValueObject, Comparable<Floor> {
    private int value; //0未用，负数为地下层，正数为地面层

    public Floor(int value) {
        this.value = value;
    }

    public static Floor of(int value) {
        if (value == 0) {
            throw new IllegalArgumentException("楼层只能为正整数或者负整数");
        }
        return new Floor(value);

    }

    @Override
    public int compareTo(Floor to) {
        return this.value - to.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}

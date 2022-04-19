package com.robustel.dispatching.domain.elevator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.yeung.api.ValueObject;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@ToString
@Getter
@NoArgsConstructor
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
        return this.value - to.getValue();
    }
}

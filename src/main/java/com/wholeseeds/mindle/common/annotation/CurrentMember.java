package com.wholeseeds.mindle.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인된 회원 정보를 Controller 메서드 파라미터로 주입하는 애노테이션
 * `@RequireAuth`이 적용된 컨트롤러에서 사용 가능
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentMember {
}

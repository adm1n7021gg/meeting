package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

/**
 * Spring Securityの設定を行うクラス。
 * 1,ConfigurationとEnableWebSecurityアノテーションを付ける
 * 2,WebSecurityConfigurerAdapterを継承する
 * の2つが必要。
 * 
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/** ログイン処理を行うインスタンスをDI */
	private final UserDetailsService uds;

	/**
	 * 基本的な設定はここで行う。
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
 
		// アクセス権限に関する設定
		http
			// /はアクセス制限をかけない
			.authorizeRequests().antMatchers("/login").permitAll()
			// /adminはADMINロールを持つユーザだけアクセス可能
			.antMatchers("/admin").hasRole("ADMIN")
			// /userはUSERロールを持つユーザだけアクセス可能
			.antMatchers("/user").hasRole("USER")
			// それ以外のページは認証が必要
			.anyRequest().authenticated();

		// ログインに関する設定
		http
			.formLogin()
			// ログインを実行するページを指定。
			// この設定だと/にPOSTするとログイン処理を行う
			.loginProcessingUrl("/")
			// ログイン画面の設定
			.loginPage("/")
			// ログインに失敗した場合の遷移先
			.failureUrl("/")
			// ユーザIDとパスワードのname設定
			.usernameParameter("username")
			.passwordParameter("password")
			// ログインに成功した場合の遷移先
			.defaultSuccessUrl("/common", true);
		
		// ログアウトに関する設定
		http
			.logout()
			// ログアウト処理を行うページ指定、ここにPOSTするとログアウトする
			.logoutUrl("/logout")
			// ログアウトした場合の遷移先
			.logoutSuccessUrl("/");

		// @formatter:on
	}


	/**
	 * パスワードのハッシュ化を行うアルゴリズムを返す
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * ログイン処理の設定
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// ログイン処理時のユーザー情報をDBから取得する
		auth.userDetailsService(uds).passwordEncoder(passwordEncoder());
	}

}


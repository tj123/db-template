package com.github.tj123.db.operate;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于查询的参数
 */
public class Sql {
	
	private StringBuilder content;
	
	private List<Object> params;
	
	public StringBuilder getContent() {
		return content;
	}

	public Sql newContent(){
		synchronized (this){
			if(content == null){
				this.content = new StringBuilder();
			}
		}
		return this;
	}

	public Sql newSql(){
		return newContent().newParams();
	}

	public Sql newParams(){
		synchronized (this){
			if(params == null){
				params = new ArrayList<>();
			}
		}
		return this;
	}
	
	public Sql setContent(StringBuilder content) {
		this.content = content;
		return this;
	}
	
	public Sql append(Sql sql){
		this.content.append(sql.getContent());
		this.params.addAll(sql.getParams());
		return this;
	}

	public Sql append(StringBuilder content){
		this.content.append(content);
		return this;
	}

	public Sql append(String content){
		this.content.append(content);
		return this;
	}

	public Sql append(List<Object> params){
		this.params.addAll(params);
		return this;
	}

	public List<Object> getParams() {
		return params;
	}
	
	public Sql setParams(List<Object> params) {
		this.params = params;
		return this;
	}
}

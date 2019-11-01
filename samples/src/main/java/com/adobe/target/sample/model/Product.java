/*
 * Copyright 2019 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.target.sample.model;

import javax.validation.constraints.NotEmpty;
import java.util.Calendar;

public class Product {

	private Long id;

	@NotEmpty(message = "Image url is required.")
	private String image;

	@NotEmpty(message = "Summary is required.")
	private String summary;

	private Calendar created = Calendar.getInstance();

	public Product(Long id, @NotEmpty(message = "Image url is required.") String image, @NotEmpty(message = "Summary is required.") String summary) {
		this.id = id;
		this.image = image;
		this.summary = summary;
	}

	public Product() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getCreated() {
		return this.created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String text) {
		this.image = text;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}

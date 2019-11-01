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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryProductRepository implements ProductRepository {

	private static AtomicLong counter = new AtomicLong();

	private final ConcurrentMap<Long, Product> messages = new ConcurrentHashMap<>();

	@Override
	public Iterable<Product> findAll() {
		return this.messages.values();
	}

	@Override
	public Product save(Product product) {
		Long id = product.getId();
		if (id == null) {
			id = counter.incrementAndGet();
			product.setId(id);
		}
		this.messages.put(id, product);
		return product;
	}

	@Override
	public Product findMessage(Long id) {
		return this.messages.get(id);
	}

	@Override
	public void deleteMessage(Long id) {
		this.messages.remove(id);
	}

}

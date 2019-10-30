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
package com.adobe.target.sample.controller;

import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.sample.service.TargetClientService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.adobe.target.sample.model.Product;
import com.adobe.target.sample.model.ProductRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * Sample controller with actual web page with dummy products.
 * Demonstrates offers being applied by at.js without making network call.
 * Only root path / and /{id} has target enabled on it.
 */
@Controller
@RequestMapping("/")
public class ProductController {

    private final ProductRepository productRepository;

    private final TargetClientService targetClientService;

    public ProductController(ProductRepository productRepository, TargetClientService targetClientService) {
        this.productRepository = productRepository;
        this.targetClientService = targetClientService;
    }

    /**
     * Returns the list of products to be displayed. Sets the response received from target in serverState
     * variable which is finally applied by at.js in the browser.
     */
    @GetMapping
    public ModelAndView list(HttpServletRequest request, final HttpServletResponse response) {
        TargetDeliveryResponse targetDeliveryResponse = targetClientService.getPageLoadTargetDeliveryResponse(request
                , response);
        Map<String, Object> models = new HashMap<>();
        models.put("serverState", targetDeliveryResponse.getServerState());
        Iterable<Product> messages = this.productRepository.findAll();
        models.put("products", messages);
        return new ModelAndView("messages/list", models);
    }

    /**
     * Returns the product with specified ID. It also returns the response received from target which is
     * eventually applied by at.js.
     */
    @GetMapping("{id}")
    public ModelAndView view(@PathVariable("id") Product product, HttpServletRequest request,
                             final HttpServletResponse response) {
        TargetDeliveryResponse targetDeliveryResponse = targetClientService.getPageLoadTargetDeliveryResponse(request
                , response);
        Map<String, Object> models = new HashMap<>();
        models.put("serverState", targetDeliveryResponse.getServerState());
        models.put("product", product);
        return new ModelAndView("messages/view", models);
    }

    @GetMapping(params = "form")
    public String createForm(@ModelAttribute Product product) {
        return "messages/form";
    }

    @PostMapping
    public ModelAndView create(@Valid Product message, BindingResult result,
                               RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return new ModelAndView("messages/form", "formErrors", result.getAllErrors());
        }
        message = this.productRepository.save(message);
        redirect.addFlashAttribute("globalMessage", "view.success");
        return new ModelAndView("redirect:/{product.id}", "product.id", message.getId());
    }

    @RequestMapping("foo")
    public String foo() {
        throw new RuntimeException("Expected exception in controller");
    }

    @GetMapping("delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        this.productRepository.deleteMessage(id);
        Iterable<Product> messages = this.productRepository.findAll();
        return new ModelAndView("messages/list", "messages", messages);
    }

    @GetMapping("modify/{id}")
    public ModelAndView modifyForm(@PathVariable("id") Product product) {
        return new ModelAndView("messages/form", "message", product);
    }

}

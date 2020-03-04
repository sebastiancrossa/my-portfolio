// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.util.ArrayList;
import java.util.List;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  Translate translate = TranslateOptions.getDefaultInstance().getService();
  Translation translation;
  String translatedText;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    
    for (Entity entity : results.asIterable()) {
        long id = entity.getKey().getId();
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");

        translation = translate.translate(text, Translate.TranslateOption.targetLanguage("es"));
        translatedText = translation.getTranslatedText();

        Comment comment = new Comment(id, translatedText, timestamp);
        comments.add(comment);
    }

    Gson gson = new Gson();
    String json = gson.toJson(comments);

    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Getting the text from the form inputs
      String text = request.getParameter("comment-text");
      long timestamp = System.currentTimeMillis();

      // Creating a new Comments entity with our text property
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("text", text);
      commentEntity.setProperty("timestamp", timestamp);

      // Pushing our comment object to our DataStore database
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);

      // Respond the request with the entered comment
      response.sendRedirect("/index.html");
  }
}

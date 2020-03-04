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

/**
 * Fetches the data from our Java servlet and parses the received JSON
 */
const getComments = async () => {
    let commentsList = document.getElementById('comments-container');
    let commentsListItem;

    const res = await fetch('/data');
    const data = await res.text();

    // Parsing our fetched JSON so it can be mapped through later on
    const parsedData = JSON.parse(data); 

    // Creating a single instance of an <li> component based on the comments
    parsedData.map(comment => {
        commentsListItem = document.createElement('li');
        commentsListItem.innerHTML = comment.text;

        commentsList.appendChild(commentsListItem);
    });
}
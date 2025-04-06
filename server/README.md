## Introduction
This document contains the relevant design and implementation aspects of the Epicurius Mobile Application.

The __domain__ of the system contains the ``User``, ``Fridge``, ``Product``, ``Recipe``, ``Collections`` and ``Meal Planner`` entities which are described as follows:

* A _user_ is characterized by an unique number, an unique username, an unique email, an unique token, a password, a country, privacy, intolerance list, diet list and a profile picture;

* A _fridge_ is characterized by a list of _product_ characterized by product name, an unique entry number, quantity, open date and expiration date;

* A _recipe_ is characterized by a unique number, the recipe's name, the id of the author, a description, servings, preparation time, type of cuisine, type of meal, a list of intolerances, a list of diets, the ingredients list, number of calories, proteins, fat, carbohydrates, a map with the instructions and a list of images;

* _collection_

* _meal planner_

The __backend__ was developed using __Kotlin__ technology. In order t  __handle/receive HTTP requests__, the [__Spring MVC__](https://docs.spring.io/spring-framework/reference/web/webmvc.html) library was used. The specific __data__ of the application is __stored__ in a __Postgresql database__, __Firestore database__ and __Cloud Storage__.


## Modeling the database
### Conceptual model

![Entity-Relationship Model](./imgs/ModeloEA.png)

#### Design Restrictions

### Physical Model

![Physical Model](./imgs/ER.png)

#### Design Aspects

## Software organization

### Open-API Specification
The YAML file for the current implementation is present [here](api.yml).

The __routes__ for the __API__ are shown below:

| Method        | Route                                     | Description                               | Authenticated |
|---------------|-------------------------------------------|-------------------------------------------|---------------|
| GET           | ``/api/signup``                           | Signup new user to the system             | No            |
| POST          | ``/api/login``                            | Login a user user                         | No            |
| PATCH         | ``/api/user/password``                    | Reset user's password                     | No            |
|               |                                           |                                           |               |
|               |                                           |                                           |               |
| GET           | ``/api/users``                            | Search user                               | Yes           |
| GET           | ``/api/users/profiles/{username}``        | Get user's profile                        | Yes           |
| GET           | ``/api/user``                             | Get a user                                | Yes           |
| GET           | ``/api/user/intolerances``                | Get user's intolerances                   | Yes           |
| GET           | ``/api/user/diets``                       | Get user's diets                          | Yes           |
| GET           | ``/api/user/follow-requests``             | Get user's follow requests                | Yes           |
| GET           | ``/api/user/followers``                   | Get user's followers                      | Yes           |
| GET           | ``/api/user/following``                   | Get user's following users                | Yes           |
| POST          | ``/api/logout``                           | Logout a user user                        | Yes           |
| PATCH         | ``/api/user``                             | Update a user                             | Yes           |
| PATCH         | ``/api/user/picture``                     | Update user's profile picture             | Yes           |
| PATCH         | ``/api/user/follow/{username}``           | Follow a user                             | Yes           |
| PATCH         | ``/api/user/follow-requests/{username}``  | Add a request to follow a user            | Yes           |
| DELETE        | ``/api/user/follow/{username}``           | Unfollow a user                           | Yes           |
|               |                                           |                                           |               |
|               |                                           |                                           |               |
| GET           | ``/api/fridge``                           | Get user's fridge                         | Yes           |
| GET           | ``/api/products``                         | Get valid products to add                 | Yes           |
| POST          | ``/api/fridge``                           | Add product to the fridge                 | Yes           |
| PATCH         | ``/api/fridge/product/{entryNumber}``     | Update product in the fridge              | Yes           |
| PATCH         | ``/api/fridge/open/{entryNumber}``        | Add open date to fridge product           | Yes           |
| DELETE        | ``/api/fridge/product/{entryNumber}``     | Delete product from fridge                | Yes           |


### Request Details

#### __API__

#### __Services__


### Connection Management

### Error Handling/Processing

## Critical Evaluation

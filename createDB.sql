-- create Business table
CREATE TABLE Business (
    business_id     VARCHAR(100),
    city            VARCHAR(50),
    state           VARCHAR(30),
    name            VARCHAR(150) NOT NULL,
    stars           NUMBER,
    PRIMARY KEY (business_id)
);
-- create Business category table
CREATE TABLE MainCategory (
    business_id     VARCHAR(100),
    mainCategory   VARCHAR(150),
    PRIMARY KEY (business_id, mainCategory),
    FOREIGN KEY (business_id) REFERENCES Business
);
-- create Business subcategory table
CREATE TABLE SubCategory (
    business_id     VARCHAR(100),
    subCategory     VARCHAR(150),
    PRIMARY KEY (business_id, subCategory),
    FOREIGN KEY (business_id) REFERENCES Business
);
-- create Attribute table
CREATE TABLE Attribute (
    business_id     VARCHAR(100),
    attribute       VARCHAR(200),
    PRIMARY KEY (business_id, attribute),
    FOREIGN KEY (business_id) REFERENCES Business
);
-- create YelpUser table
CREATE TABLE YelpUser (
    user_id         VARCHAR(50),
    name            VARCHAR(150) NOT NULL,
    yelping_since   VARCHAR(20),
    review_count    INTEGER,
    average_stars   NUMBER,
    friend_count    INTEGER,
    votes           INTEGER,
    PRIMARY KEY (user_id)
);
-- create Review table
CREATE TABLE Review (
    review_id       VARCHAR(100),
    business_id     VARCHAR(100) NOT NULL,
    user_id         VARCHAR(100) NOT NULL,
    review_date     VARCHAR(20),
    stars           INTEGER,
    votes           INTEGER,
    PRIMARY KEY (review_id),
    FOREIGN KEY (business_id) REFERENCES Business,
    FOREIGN KEY (user_id) REFERENCES YelpUser
);


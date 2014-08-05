DROP TABLE IF EXISTS tbl_item;

DROP TABLE IF EXISTS tbl_supplier;

DROP TABLE IF EXISTS tbl_supplier_category;

DROP TABLE IF EXISTS tbl_distributor;

DROP TABLE IF EXISTS tbl_outlet;

DROP TABLE IF EXISTS tbl_city;

DROP TABLE IF EXISTS tbl_route;

DROP TABLE IF EXISTS tbl_district;

DROP TABLE IF EXISTS tbl_order_detail;

DROP TABLE IF EXISTS tbl_order;

DROP TABLE IF EXISTS tbl_free_issue_ratio;

CREATE TABLE tbl_distributor (
    distributorId INTEGER PRIMARY KEY,
    distributorName TEXT
);

CREATE TABLE tbl_supplier_category (
    supplierCategoryId INTEGER,
    distributorId INTEGER,
    supplier TEXT
);

CREATE TABLE tbl_supplier (
    supplierId INTEGER,
    distributorId INTEGER,
    supplierCategoryId INTEGER,
    supplierName TEXT NOT NULL
);

CREATE TABLE tbl_item (
    itemId INTEGER,
    distributorId INTEGER,
    supplierCategoryId INTEGER,
    supplierId INTEGER not null,
    itemCode TEXT,
    itemDescription TEXT CHECK (itemDescription != ''),
    price DECIMAL(10 , 2) NOT NULL DEFAULT 0,
    packSize Text,
    stock int default 0
);

CREATE TABLE tbl_district (
    districtId INTEGER PRIMARY KEY,
    districtName TEXT NOT NULL
);

CREATE TABLE tbl_route (
    routeId INTEGER PRIMARY KEY,
    districtId INTEGER NOT NULL REFERENCES tbl_district (districtId) ON UPDATE CASCADE,
    routeName TEXT NOT NULL
);

CREATE TABLE tbl_city (
    cityId INTEGER PRIMARY KEY,
    routeId INTEGER NOT NULL REFERENCES tbl_route (routeId) ON UPDATE CASCADE,
    cityName TEXT NOT NULL
);

CREATE TABLE tbl_outlet (
    outletId INTEGER PRIMARY KEY,
    cityId INTEGER NOT NULL REFERENCES tbl_city (cityId) ON UPDATE CASCADE,
    outletName TEXT NOT NULL,
    outletAddress TEXT NOT NULL,
    outletType INT NOT NULL DEFAULT 0,
    outletDiscount REAL DEFAULT 0 CHECK (outletDiscount >= 0 AND outletDiscount <= 100)
);

CREATE TABLE tbl_order (
    orderId INTEGER PRIMARY KEY AUTOINCREMENT,
    outletId INTEGER REFERENCES tbl_outlet (outletId) ON UPDATE CASCADE,
    routeId INTEGER NOT NULL,
    positionId INTEGER NOT NULL,
    invoiceTime LONG,
    total REAL,
    batteryLevel INTEGER NOT NULL,
    longitude REAL NOT NULL,
    latitude REAL NOT NULL,
    distributorId INT
);

CREATE TABLE tbl_order_detail (
    orderId INTEGER NOT NULL REFERENCES tbl_order (orderId) ON UPDATE CASCADE,
    itemId INTEGER NOT NULL REFERENCES tbl_item (itemId) ON UPDATE CASCADE,
    price DECIMAL(10 , 2 ) NOT NULL,
    discount REAL,
    quantity INT,
    freeQuantity INT DEFAULT 0,
    returnQuantity INT DEFAULT 0,
    replaceQuantity INT DEFAULT 0,
    sampleQuantity INT DEFAULT 0,
    PRIMARY KEY (orderId , itemId)
);

CREATE TABLE tbl_free_issue_ratio (
    freeIssueId INTEGER PRIMARY KEY AUTOINCREMENT,
    itemId INTEGER NOT NULL,
    rangeMinimumQuantity INT NOT NULL DEFAULT 0,
    freeIssueQuantity INT NOT NULL DEFAULT 0
);

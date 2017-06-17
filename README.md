OSS/BSS system

Developing system`s main aim is to automate sale of services and their management, and cooperation with customers to make this process more easier and less manual.

Description of system:
1) The system should include next user roles for interaction with the system:
    - Residential client
    - Business client
    - Employee
    - Administrator
    - CSR
    - Problem Management Group

2) The system should allow user to login through a web interface. For login user will use his email.

3) The system should have a possibility to display at the start page information about services for both residential and business clients.

4) The system should automatically determine unregistered user’s region according to his ip-address. For default data, system use Kiev region.

5) The system should provide the possibility for user with role “Residential client” to register in system with entering data (with future possibility to change it):
    - Email address
    - Telephone number
    - Address
    - First name
    - Last name

6) The system should provide the possibility for user with role “CSR” to create user with role “Residential client” , “Business client” with parameters:
    - First name
    - Last name
    - Email
    - Telephone number
    - Address
    - Company (only for “Business client”)
7) The system should provide the possibility for user with role “Business client” to create user with role “Employee” with parameters:
    - First name
    - Last name
    - Email
    - Telephone number
    - Address
    - Company

8) The system should provide the possibility for user with role “Administrator” to create users with next roles:
    - CSR
    - Problem Management Group
    - Administrator

9) The system should provide the possibility for user with role “Administrator” to block user

10) The system should provide the possibility for user with role “CSR” to create customers with parameters:
    - Name
    - Secret code

11) The system should provide the possibility for user with role “Administrator” to create, edit and delete services
with parameters:
    - Name
    - Category (only while create)
    - Duration
    - Price
    - Description
    - Region of distribution
    - Need for additional processing
    - Status

12) The system should provide the possibility for user with role “Administrator” to create, edit and delete tariff plans with parameters:
    - Name
    - Category
    - Duration
    - Price
    - Region of distribution
    - List of included services
    - Need for additional processing
    - Status

13) The system should provide the possibility for user with role “Administrator” to configure price of product according to region of country, where it distributed.

14) The system should provide the possibility to change the currency of prices

15) The system should provide the possibility for user with role “Administrator” to create category products with parameters:
    - Name
    - Description

16) The system should provide the possibility for user with role “Administrator” to configure discount for product or group of products in product catalog, and discount can be based on criteria:
    - Start and end date
    - Region or group of regions

17) The system should provide the possibility for user with role “Residential client” and “Business client” to view his order history with parameters:
    - Operation date
    - Product name
    - Product status

18) The system should provide the possibility for user with role “Employee”, “Residential client” and “Business client” to view his services/tariff plans

19) The system should provide the possibility for user with role “Residential client” to manage(activate/suspend/disable) his services/tariff plans

20) The system should provide the possibility for user with role “Business client” to manage(activate/suspend/disable) services/tariff plans for his company

21) The system should provide the possibility for user with role “CSR” to view order history, complaint history and manage(activate/suspend/disable) services/tariff plans for chosen client

22) The system should provide the possibility for user with role “Problem Management Group” to view personal data, order history, complaint history and services/tariff plans of chosen client

23) The system should automatically send e-mail, based on pattern to user with role “Employee”, “Residential client” and “Business client” for notification about events:
    - Successful registration
    - Password change
    - Changes that makes client with his current enabled services
    - Change of complaint’s status
    - Disabling products by administrator
    - Creating products by administrator

24) The system should provide the possibility for user with role “Administrator” to start marketing campaign by sending email, based on email pattern, for clients from chosen regions.

25) The system should provide the possibility for user with role “Employee”, “Residential client” and “Business client” to create complaints by filling special form on site

26) The system should provide the possibility for user with role “Residential client”, “Business client” and “Employee” to view his complaint history with parameters:
    - Complaint status
    - Product name
    - Complaint date

27) The system should provide the possibility for user with role “CSR” to create complaints on behalf of the client

28) The system should provide the possibility for user with role “Problem Management Group” to change status of complaints

29) The system should provide the possibility for user with role “Problem Management Group” to send response for complaint to client on email.

30) The system should provide the possibility for user with role ”CSR“ to generate reports and export them into formats: xls/xlsx based on information about orders history of client or group of clients for a certain period of time.

31) The system should provide the possibility for user with role ”CSR“ to create a graphs based on information about orders history of client or group of clients for a certain period of time.
Information used for graphs:
    - Region of distribution
    - Product’s date of activate

32) The system should provide the possibility for user with role ”Problem Management Group“ to generate reports and export them into formats: xls/xlsx based on information about complaints history of client or group of clients for a certain period of time.

33) The system should provide the possibility for user with role ”Problem Management Group“ to create a graphs based on information about complaints history of client or group of clients for a certain period of time.
Information used for graphs:
    - Clients region
    - Complaint’s create date

34) The system should provide the possibility for user with roles “Administrator” and ”CSR“ to sort the list of products according to next criteria:
    - Name
    - Type
    - Region of distribution
    - Price

35) The system should provide the possibility for all users to search all products by the following criteria:
    - Name
    - Type
    - Region of distribution
    - Price

36) The system should provide the possibility for user with roles “CSR”, “Problem Management Group” to search all client’s accounts by the following criteria:
    - First name
    - Last name
    - Address
    - Telephone number
    - Company
    - Email address

37) The system should provide the possibility for user with role ”Administrator” to search all user’s accounts by the following criteria:
    - First name
    - Last name
    - Email
    - User category

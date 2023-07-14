# SEIMS
----------------
State Educational Institution Monitoring Service. Project for VSPU developed by Bogdan Shevtsov. 2022-2023.
----------------
### Project goal: 
-
It is necessary to develop a system in the form of a server application for working with databases on educational institutions of the Voronezh region in order to systematize and generalize information arrays 
and relieve excessive load on municipal and federal channels.
**The scope of the system** is public and private educational organizations in the Voronezh region
----------------
### Product functional requirements: 
- Authorization and authentication system
- organization search and data viewing
- data filtering and sorting
- data editing
- data reading and generation.
----------------
### Minimum system functionality:
1. Storage of data about the educational organization and its branches or a list of organizations.
2. Entering and updating data.
3. Reading data from documents of a given sample in MS-Excel format: xls, slsx and csv.
4. Generation of a MS-Excel document from the organization's data according to the selected sample.
5. Authorization system and data access levels.
6. Restriction of access to data to unauthorized users.
7. Means of filtering, sorting and searching for data.
8. Storage of file applications in the format of documents, pictures or text files.
9. Administration tools.
10. Logging system with a choice of level.
----------------
### Data Requirements:
-
The key set of data that the system interacts with is quantitative (numerical) data, which can be presented as tables in Excel files (.xls, .xlsx, .csv) or stored in a database. 
The system also supports uploading media files and images (.jpeg, .png, .bmp, etc.) and text documents (.docx, .odt, .pdf).
----------------
### User environment:
Each user is assigned a different environment based on their level of access to the system. Access levels start from unauthorized user to system administrator.
- an unauthorized user has the right only to view a limited set of data;
- a representative of the organization has access to view and change the data of one or more organizations, the ability to download applications and access to reading and generation functions;
- representative of the state authorities have access to view all data, as well as to all the functions of analysis, filtering and obtaining statistics on all data;
- the system administrator has access to the server management tools and can manage the access levels of system users.
----------------
### The site was presented as the following set of web pages:
    1. **"Authorization"** (URL: /login). Here is the authorization panel in the system.
    2. **"Monitoring"** or the main page (URL: /view/monitoring). This page contains an interactive map where you can view a list of districts of the Voronezh region, including districts of the city of Voronezh, and a list of organizations for the selected area, with the ability to filter. Also above the map is a navigation bar, which allows you to navigate through the main sections of the site.
    3. **"Parameter filter"** (URL: /filter). This page contains a panel for filtering database sections for the selected document, with the ability to select rows and columns of selected tables by additional criteria. After selecting the required parameters, the user can click on the "APPLY" button, then a table with the received data will be displayed below.
    4. **"Organization"** (URL: /view/org/{organization code}). This page contains all the data related to the organization, such as: name, district, contact details of the organization's representatives, website if available, description, as well as data from the forms OO-1 and OO-2, presented in tabular form. Below is the document display panel, where you can select a specific document or display both and the page navigation bar. Immediately after them, a section with interactive tables begins, in which you can see the data, as well as edit it by going to the "Edit" section. The tables, broken down by forms of study, have also been grouped for greater convenience. Even lower, the page navigation bar and the filtering bar are duplicated. And also, there is a section with file applications.
    5. **"Editing"** (URL: /edit/org/ {organization code} ?doc={document number}). In terms of content, this page repeats the “Organizations” page, with the difference that here it is possible to make changes to the data.
    6. **"Applications and Files"** (URL: /edit/org/{organization code}/apps). On this page, you can download or delete applications, download or generate Excel files. When you click on the "Upload document to the database" button, the user will be redirected to the "Upload data" page.
    7. **"Upload data"** (URL: /edit/org/ {organization code}/upload /excel). On this page, you can view and upload an Excel document to the database.
    8. **"View data"** (URL: /data/get/table/{table name in the database}). This page is available only to the system administrator and gives direct access to work with the database.

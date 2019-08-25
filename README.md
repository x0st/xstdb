# What the heck?
Just a domestic database written in Java (WIP).

# Supported queries

### Create a table
Syntax:
```
add %table_name% %columns_definition%
```

Samples:
- `add my_table [id INTEGER] [my_col STRING]`
- `add anotherone [id INTEGER] [my_col STRING] [col STRING] [my STRING]`
- `add table [id INTEGER]`

### Show table's info
Syntax:
```
show %table_name%
```

Samples:
- `show my_table`

### Insert a record
Syntax:
```
put %table_name% %column_list% %data%
```

Samples:
- `put my_table [id my_col another_col] [25 somestring "some string"]`
- `put my_table [id] [25]`
- `put users [name] ["David"]`
- `put users [name] [David]`

### Select records
Syntax:
```
get %table_name%
```
```
get %table_name% %conditions% where %placeholders%
```

Samples:
- `get my_table`
- `get users [id = :id] where [:id = 1]`
- `get customers [age > :age & city = :city] where [:age = 20 & :city = London]`
- `get users [id > :id | name = :name] where [:name = "John" & :id = 50]`

# How data is stored
**xstdb** utilizes two files to store data: *%table%.dat* and *%table%.def*. The first one contains records, the latter - table's definition.

### %table%.def's structure


| purpose                      | type      | bytes taken |
|------------------------------|---------- |-------------|
| table definition (file size) | SHORT INT | 2 bytes     |
| rows count                   | INTEGER   | 4 bytes     |
| table name's length          | INTEGER   | 4 bytes     |
| table name                   | STRING    | ? bytes     |
| number of columns            | BYTE      | 1 byte      |
| *column 1*: name's length    | INTEGER   | 4 bytes     |
| *column 1*: name             | STRING    | ? bytes     |
| *column 1*: type             | BYTE      | 1 byte      |
| *column 1*: size             | INTEGER   | 4 bytes     |
| *column 2*: name's length    | INTEGER   | 4 bytes     |
| *column 2*: name             | STRING    | ? bytes     |
| *column 2*: type             | BYTE      | 1 byte      |
| *column 2*: size             | INTEGER   | 4 bytes     |
| ................             | ........  | .......     |

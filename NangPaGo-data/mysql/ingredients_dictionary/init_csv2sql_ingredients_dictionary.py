import csv
import pymysql
import argparse

# CSV 파일 경로
csv_file_path = "../datasets/ingredients_dictionary.csv"

# SQL 파일 경로
create_tables_sql_file = "query/create_ingredients_dictionary.sql"
insert_data_sql_file = "query/insert_ingredients_dictionary.sql"

def read_sql_file(file_path):
    with open(file_path, "r", encoding="utf-8") as file:
        sql_content = file.read()
        return [stmt.strip() for stmt in sql_content.split(';') if stmt.strip()]

def execute_sql_statements(cursor, sql_statements):
    for statement in sql_statements:
        if statement:
            try:
                cursor.execute(statement)
                print(f"Executed: {statement[:50]}...")
            except Exception as e:
                print(f"Error executing statement: {e}")
                raise

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Database connection parameters")
    
    parser.add_argument("--host", type=str, required=True, help="Database host")
    parser.add_argument("--user", type=str, required=True, help="Database user")
    parser.add_argument("--password", type=str, required=True, help="Database password")
    parser.add_argument("--database", type=str, required=True, help="Database name")
    parser.add_argument("--port", type=int, required=True, help="Database port")
    
    args = parser.parse_args()

    # Connect 데이터베이스
    connection = pymysql.connect(
        host=args.host,
        user=args.user,
        password=args.password,
        database=args.database,
        port=args.port,
        local_infile=True
    )

    cursor = connection.cursor()

    create_tables_sql = read_sql_file(create_tables_sql_file)
    insert_data_sql = read_sql_file(insert_data_sql_file)

    try:
        # Create 'ingredients_dictionary' & 'refrigerator' 테이블
        execute_sql_statements(cursor, create_tables_sql)
        connection.commit()

        # Insert 'ingredients_dictionary' & 'refrigerator' 테이블
        with open(csv_file_path, "r", encoding="utf-8") as file:
            reader = csv.DictReader(file)
            for row in reader:
                id = int(row["id"])
                rcp_nm = row["name"]
                cursor.execute(insert_data_sql[0], (id, rcp_nm,))  # Assuming insert_data.sql contains 1 SQL query
                connection.commit()
                if id % 100 == 0:
                    print(f"Executed: INSERT TBALE 'ingredients_dictionary' ({id}) ...")

    except Exception as e:
        print(f"오류 발생: {e}")
        connection.rollback()

    finally:
        cursor.close()
        connection.close()

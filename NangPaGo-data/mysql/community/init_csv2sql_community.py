import pymysql
import argparse

# SQL 파일 경로
create_tables_sql_file = "query/create_community.sql"

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
    
    try:
        # Create 'ingredients_dictionary' & 'refrigerator' 테이블
        execute_sql_statements(cursor, create_tables_sql)
        connection.commit()

    except Exception as e:
        print(f"오류 발생: {e}")
        connection.rollback()

    finally:
        cursor.close()
        connection.close()

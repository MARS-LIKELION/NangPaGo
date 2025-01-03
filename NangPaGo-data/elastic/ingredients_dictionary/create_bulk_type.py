import csv
import json

# 입력 데이터 파일 : NangPaGo/NangPaGo-data/datasets/ingredients_dictionary.csv'
# 출력 데이터 파일 : NangPaGo/NangPaGo-data/elastic/ingredients_dictionary/ingredients_dictionary.json

if __name__ == "__main__":
    with open('../../datasets/ingredients_dictionary.csv', mode='r', encoding='utf-8') as file:
        reader = csv.DictReader(file)

        with open("ingredients_dictionary.json", "w", encoding="utf-8") as f:
            for row in reader:
                f.write(f'{{ "index":{{ "_index" : "ingredients_dictionary", "_type" : "_doc" }} }}\n')
                f.write(json.dumps({"name": row["name"]}, ensure_ascii=False) + "\n")

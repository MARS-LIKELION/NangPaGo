import pandas as pd
import numpy as np
import re
from collections import Counter

def extract_name(text):
    edited = re.sub(r'\d+.*', '', text) # 15g
    edited = re.sub(r'[^\w\s가-힣]$', '', edited) # '한글(', '-'

    matches = re.findall(r'.*\n(.*)', edited)
    if matches:
        result = matches[-1]  # print(edited, '^^^', matches[-1])
    else:
        result = edited
    
    result = re.sub(r'[●\•\·\-]\s?[가-힣]+', '', result) # '●곤약냉체 ' 삭제
    result = re.sub(r'.*밑간\s|.*재료\s', '', result) # 닭고기 삶는 재료
    result = re.sub(r'[:\"+\]]', "", result)
    result = re.sub(r'^[^가-힣a-zA-Z]+$', '', result)
        
    result = result.strip()
    # 예외 처리
    if (result in ["소스", "드레싱", "양념", "양념장", "육수", ")", "•", "●", "·", "<br", "미니"]):
        result = ""
    
    return result

def extract_food_names_from_list(text_list):
    food_names_list = list()
    
    for text in text_list:
        text = text[0]
        text = re.sub(r'[\[\(][^\]\)]*[\]\)]|<[^>]+>', ',', text) # [] () <> HTML 태크 제거
        text = re.sub(r'\d+인분|소스+\d|소스\s|드레싱\s|양념\s|양념장\s|육수\s', lambda m: m.group(0).strip() + ', ', text)
        text = re.sub(r'적당량', "", text)
        text = re.sub(r'[●\•\-\s]?[a-zA-Z0-9가-힣]+ ?:', ',', text)
        
        items = text.split(',')
        
        food_names = list()
        for item in items:
            name = extract_name(item)
            if name:
                food_names.append(name)
        
        food_names_list.extend(food_names)  # 리스트로 추가
    
    return set(food_names_list) # 집합


if __name__ == "__main__":
    fname = "datasets/food_raw.csv"
    df_food = pd.read_csv(fname).values.tolist()

    food_names = sorted(list(extract_food_names_from_list(df_food)))
    df_food_csv = pd.DataFrame(food_names, columns=["RCP_PARTS_DTLS"])
    df_food_csv.to_csv("datasets/food_dict.csv", index=False, encoding="utf-8")

    print(len(food_names))
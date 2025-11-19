import csv

PATH = r'C:\Users\ericj\Desktop\HW\CS336\wwf\StaticData\simplemaps_worldcities_basicv1.901\worldcities.csv'

def load_city_data():
    city_data = []
    with open(PATH, mode='r', encoding='utf-8') as file:
        reader = csv.DictReader(file)
        for row in reader:
            city_data.append(row)
    return city_data

data = load_city_data()

txt = ""
numCities = 0
for city in data:
    
    if not (city['population'] and city['population'].isdigit()):
        continue
    
    if city['country'] == 'United States':
        if int(city['population']) < 100000:
            continue
    else:
        if int(city['population']) < 2000000:
            continue

    # print(city['city_ascii'], city['population'])
    numCities += 1
    txt += f"{city['city_ascii']}, {city['country']}, {city['lat']}, {city['lng']}, {city['population']}, {city['id']}\n"
print(f"Number of cities: {numCities}")

# write txt to ./CityData.txt
with open('CityData2.txt', mode='w', encoding='utf-8') as file:
    file.write(txt)
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from bs4 import BeautifulSoup
import re
import time

# data ansvarlig
# gdpr officer
# hvad bliver data brugt til, værdi?
# projekt hos SDU, meget kort fattet projekt
# App som kan indsamle data omkring folk købsvaner og sådan noget.

BASE_URLS = {
    "Bilka": "https://www.bilkatogo.dk",
    "Føtex": "https://koeboghent.foetex.dk"
}
DATA_FOLDER = "C:\\Users\\kazim\\Desktop\\data\\"
f = open(DATA_FOLDER + "data.csv", "w")
f.write("product,productSize,productSizeType,unitPrice,unitPriceType,productPrice,productType,store\n")


def main():
    """
    This program was designed to scrape Bilka-To-Gos and Køb og hent Føtexs website.
    It does this by going through each website in BASE_URLS, it visits these sites, accepts the cookies pop-up,
    then goes on to scrape the website.
    """
    driver = setup_driver()
    wait = WebDriverWait(driver, 10)
    for store, base_url in BASE_URLS.items():
        driver.get(base_url)
        accept_cookies(wait)
        time.sleep(5)  # Sleep to allow new page to load
        scrape(driver, wait, store, base_url)
    driver.close()
    driver.quit()
    f.close()


def setup_driver():
    """
    Setups up the webdriver using a headless chrome browser
    :return: the driver
    """
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--disable-gpu')
    driver = webdriver.Chrome("C:\webdriver\chromedriver.exe", options=options)
    return driver


def accept_cookies(driver_wait):
    """
    Takes the given wait driver and looks for a button with the text "Accepter alle".
    Once the button is ready it will click the button
    :param driver_wait: the wait driver
    """
    cookie_button = driver_wait.until(EC.element_to_be_clickable((By.XPATH, '//button[text()="Accepter alle"]')))
    cookie_button.click()


def get_categories(html):
    """
    Uses BeautifulSoup in order to find all <a> elements where the href is equal to
    /kategori/ and then anything beyond that /. It will then retrieve the text of that <a>
    element without visiting any children.
    :param html: the html to find the <a> elements in
    :return: a dictionary mapping the category to the url of that category
    """
    categories = {}
    soup = BeautifulSoup(html, features="html.parser")
    a_elements = soup.find_all("a", href=re.compile("^/kategori/.*"))
    for a in a_elements:
        category = a.find(text=True, recursive=False)
        if category and category.strip():
            categories[category.strip()] = a["href"]
    return categories


def scrape(driver, driver_wait, store, base_url):
    """
    This method will retrieve all categories that can be found on the current page of the driver.
    It will then loop through each of them, visit the category's endpoint.
    Here it will retrieve all <div> elements where the class is set to either
    "product-card__text", "product-card__description" or "product-price".
    It will then retrieve the text from these <div> elements.
    It will then preprocess this text, by the rules defined in the preprocess function.
    Finally it will write to the global file that is open.
    :param driver: The webdriver that is to be used.
    :param driver_wait: the wait driver that is be used.
    :param store: the name of the store who's website we are currently on.
    :param base_url: the base url of the stores website.
    """
    categories = get_categories(driver.page_source)
    for category, link in categories.items():
        print(store, ":", category)
        driver.get(base_url + link)
        load_all_products(driver_wait)
        soup = BeautifulSoup(driver.page_source, features="html.parser")
        divs = soup.find_all("div", class_=re.compile(r"product-card__text|product-card__description|product-price"))
        items = []
        for div in divs:
            items.extend(preprocess(div.text.strip()).split("\n"))
        for i, item in enumerate(items, 1):
            f.write(item)
            if i % 6 == 0:
                f.write(",")
                f.write(category)
                f.write(",")
                f.write(store)
                f.write("\n")
            else:
                f.write(",")


def load_all_products(driver_wait):
    """
    Small method that will continously look for a button on the current website that is loaded in
    the wait driver. It will look for a button that has the text "\n    Indlæs flere\n  " and keep pressing
    it as long as it shows up before the wait driver timesout. If the wait driver times out we know that we have loaded
    all sub-pages with products and we just return.
    :param driver_wait: the wait driver which is currently on the website we need to load.
    """
    try:
        while True:
            load_button = driver_wait.until(EC.element_to_be_clickable((By.XPATH, '//button[text()="\n    Indlæs flere\n  "]')))
            load_button.click()
    except TimeoutException:
        return  # If it times out, it means that we are done loading "sub pages"


def preprocess(text):
    """
    Takes a string and preprocesses it based on what data we need.
    It will first remove all newlines. It will then remove repeat spaces with a single space.
    The next substitution is because the websites have the product size and the unit prize in the same line,
    so we split these up into multiple lines. Such that the first line is the product price. The second line
    is the unit type of the product such as g, kg, stk., etc. The next line is the unit price and the last line
    specifies the unit prices type.
    The next substitution is because the websites have their floats split up such that 20,49 becomes 20 49. We therefore
    look for any string of this type and replace the space with a ",".
    The last one is simply if the text contains "," we wrap it in " such that it doesn't ruin our CSV file.
    :param text: the text that needs to preprocessed.
    :return: the processed text.
    """
    text = re.sub(r"\n", r"", text)
    text = re.sub(r" +", r" ", text)
    text = re.sub(r"^([\d.,]+)([^\d]+) ([\d.,]+)/([^\d]+)$", r"\1\n\2\n\3\n\4", text)
    text = re.sub(r"^(\d+) (\d+)$", r"\1,\2", text)
    text = re.sub(r"(.+,.+)", r'"\1"', text)
    return text


if __name__ == '__main__':
    main()

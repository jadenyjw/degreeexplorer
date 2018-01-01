import requests
from bs4 import BeautifulSoup
username = input("UTORID:")
password = input("Password:")

r = requests.get('https://degreeexplorer.utoronto.ca/degreeExplorer/', verify=False)
in_session = False
new_url = ""
jsession_id = ""
for char in r.url:
    if char == ";":
        in_session = True
    if char == "?":
        in_session = False
    if not in_session:
        new_url += char
    else:
        jsession_id += char

jsession_id = jsession_id[12:]
cookies = dict(JSESSIONID=jsession_id)
r = requests.post(new_url, verify=False, data = {'j_username':username, 'j_password': password, '_eventId_proceed':''}, cookies=cookies)
req_cookies = r.cookies
soup = BeautifulSoup(r.text, 'html.parser')
saml = soup.find("input", {"name":"SAMLResponse"})['value']
print(r.text)
cookies = dict(DPSamlSpReqURL='/degreeExplorer/', redirectUrl='http://www.acorn.utoronto.ca/dxLogout.html')
headers = {'user-agent': 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'}
r = requests.post('https://degreeexplorer.utoronto.ca/spACS', verify=False, cookies=cookies, data = {'SAMLResponse': saml}, headers=headers, allow_redirects=False)
auth = r.cookies
print(auth)

r = requests.get('https://degreeexplorer.utoronto.ca/degreeExplorer/rest/dxMenu/getStudentUserData', cookies=auth, verify=False)
auth.update(r.cookies)
r = requests.get('https://degreeexplorer.utoronto.ca/degreeExplorer/rest/dxPlanner/getPlanner', cookies=auth, verify=False)

print(r.text)

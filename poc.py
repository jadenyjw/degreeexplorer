import requests
from bs4 import BeautifulSoup
username = input("UTORID:")
password = input("Password:")

class NoQuotedCommasSession(requests.Session):
    def send(self, *a, **kw):
        # a[0] is prepared request
        print(a[0].url)

s = NoQuotedCommasSession()
s.get("http://somesite.com/an,url,with,commas,that,won't,be,encoded.")


r = requests.get('https://degreeexplorer.utoronto.ca/degreeExplorer/', verify='cert.pem')
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
print(cookies)
print(new_url)
r = requests.post(new_url, verify='cert.pem', data = {'j_username':username, 'j_password': password, '_eventId_proceed':''}, headers={'Cookie': "JSESSIONID=" + jsession_id})
print(r.headers)
req_cookies = r.cookies
soup = BeautifulSoup(r.text, 'html.parser')
saml = soup.find("input", {"name":"SAMLResponse"})['value']
cookies = dict(DPSamlSpReqURL='/degreeExplorer/', redirectUrl='http://www.acorn.utoronto.ca/dxLogout.html')
r = requests.post('https://degreeexplorer.utoronto.ca/spACS', verify='cert.pem', cookies=cookies, data = {'SAMLResponse': saml}, allow_redirects=False)
auth = r.cookies
r = requests.get('https://degreeexplorer.utoronto.ca/degreeExplorer/rest/dxMenu/getStudentUserData', cookies=auth, verify='cert.pem')
auth.update(r.cookies)
r = requests.get('https://degreeexplorer.utoronto.ca/degreeExplorer/rest/dxPlanner/getPlanner', cookies=auth, verify='cert.pem')
print(r.text)

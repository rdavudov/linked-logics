import groovy.json.JsonSlurper

def jsonString = '{"name": "John Doe", "age": 30, "address": {"city": "New York", "state": "NY"}}'
def json = new JsonSlurper().parseText(jsonString)

return json

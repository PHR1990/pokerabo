import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private platesUrl: string = 'http://localhost:8080/api/plate/';
  private kitsUrl: string = 'http://localhost:8080/api/kits/';

  constructor(private httpClient: HttpClient) { }

  public getPlates()  {
    return this.httpClient.get(this.platesUrl);
  }

  public getKits() {
    return this.httpClient.get(this.kitsUrl);
  }

  public postKit(data: Object) {

    let headers = new HttpHeaders();


    let options = {
      headers: headers
    };

    return this.httpClient.post(this.kitsUrl, data, options);
  }

  public postPlate(data : Object) {

    let headers = new HttpHeaders();


    let options = {
        headers : headers
    } ;

    return this.httpClient.post(this.platesUrl, data, options);
  }

  public deletePlate(uuid : string) {

    let headers = new HttpHeaders();
    let options = {
      headers : headers
    } ;

    return this.httpClient.delete(this.platesUrl + uuid, options).subscribe((result) => {

      console.log(result);
    });



  }
}

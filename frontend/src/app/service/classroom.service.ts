import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Assignment, AssignmentCompletion, AssignmentResponse, Classroom, CompletionRequest, Member, NewAssignment, NewClassroom } from '../model/class.model';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClassroomService {

  private url = 'http://localhost:8082/class/api/v1/classroom';
  private currentQuestionSubject: Subject<number> = new Subject<number>();


  constructor(private http: HttpClient) { }

  createClass(classroom: NewClassroom): Observable<HttpResponse<Classroom>> {
    return this.http.post<Classroom>(`${this.url}/create`, classroom, {observe: 'response'});
  }

  getClassesOwned(): Observable<HttpResponse<Classroom[]>> {
    return this.http.get<Classroom[]>(`${this.url}/owned`, { observe: 'response' })
  }

  getClassesMember(): Observable<HttpResponse<Classroom[]>> {
    return this.http.get<Classroom[]>(`${this.url}/member`, { observe: 'response' })
  }

  getClass(id: number): Observable<HttpResponse<Classroom>> {
    return this.http.get<Classroom>(`${this.url}/${id}`, { observe: 'response' })
  }

  updateClass(updatedClass: Classroom): Observable<HttpResponse<Classroom>> {
    return this.http.put<Classroom>(`${this.url}/${updatedClass.id}`, updatedClass, { observe: 'response' });
  }

  deleteClass(id: number): Observable<HttpResponse<Classroom>> {
    return this.http.delete<Classroom>(`${this.url}/${id}`, { observe: 'response' });
  }

  getAssignmentsTeacher(id: number): Observable<HttpResponse<Assignment[]>> {
    return this.http.get<Assignment[]>(`${this.url}/admin/assignments/${id}`, { observe: 'response' });
  }

  getAssignmentsMember(id: number): Observable<HttpResponse<AssignmentResponse[]>> {
    return this.http.get<AssignmentResponse[]>(`${this.url}/member/assignments/${id}`, { observe: 'response' });
  }

  getAssignment(id: number): Observable<HttpResponse<Assignment>> {
    return this.http.get<Assignment>(`${this.url}/assignment/${id}`, { observe: 'response' });
  }

  getAssignments(): Observable<HttpResponse<Assignment[]>> {
    return this.http.get<Assignment[]>(`${this.url}/member/assignments`, { observe: 'response' });
  }

  getAssignmentsAdmin(): Observable<HttpResponse<Assignment[]>> {
    return this.http.get<Assignment[]>(`${this.url}/admin/assignments`, { observe: 'response' });
  }

  joinClass(joinCode: string): Observable<HttpResponse<Member>> {
    return this.http.post<Member>(`${this.url}/join`, joinCode, { observe: 'response' });
  }

  createAssignment(assignment: NewAssignment): Observable<HttpResponse<Assignment>> {
    return this.http.post<Assignment>(`${this.url}/assign`, assignment, { observe: 'response' });
  }

  submitAssignment(completion: CompletionRequest): Observable<HttpResponse<AssignmentCompletion>> {
    return this.http.post<AssignmentCompletion>(`${this.url}/submit`, completion, { observe: 'response' });
  }

  getAssignmentCompletionsAdmin(id: number): Observable<HttpResponse<AssignmentCompletion[]>> {
    return this.http.get<AssignmentCompletion[]>(`${this.url}/admin/completions/${id}`, { observe: 'response' });
  }

  getAllCompletionsInClass(classId: number): Observable<HttpResponse<AssignmentCompletion[]>> {
    return this.http.get<AssignmentCompletion[]>(`${this.url}/admin/${classId}/completions`, { observe: 'response' });
  }

  getAllCompletionsByAdminId(): Observable<HttpResponse<AssignmentCompletion[]>> {
    return this.http.get<AssignmentCompletion[]>(`${this.url}/admin/completions`, { observe: 'response' });
  }

  removeStudent(memberId: number, classId: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.url}/${classId}/${memberId}`, { observe: 'response' });
  }

  getAllCompletionsMember(classId: number, memberId: number): Observable<HttpResponse<AssignmentCompletion[]>> {
    return this.http.get<AssignmentCompletion[]>(`${this.url}/admin/${classId}/${memberId}/completions`, { observe: 'response' });
  }

  getMostRecentCompletions(): Observable<HttpResponse<AssignmentCompletion[]>> {
    return this.http.get<AssignmentCompletion[]>(`${this.url}/member/completions`, { observe: 'response' });
  }

  getMostRecentCompletionsForEachClassroom(): Observable<HttpResponse<AssignmentCompletion[][]>> {
    return this.http.get<AssignmentCompletion[][]>(`${this.url}/member/completions/classroom`, { observe: 'response' });
  }

  getAllCompletionsByMember(): Observable<HttpResponse<AssignmentCompletion[]>> {
    return this.http.get<AssignmentCompletion[]>(`${this.url}/member/completions/all`, { observe: 'response' });
  }

  getCompletionsForEachClassroomTeacher(): Observable<HttpResponse<AssignmentCompletion[][]>> {
    return this.http.get<AssignmentCompletion[][]>(`${this.url}/admin/completions/classroom`, { observe: 'response' });
  }

  getAverageForAllAssignments(classId: number): Observable<HttpResponse<Record<string, number>>> {
    return this.http.get<Record<string, number>>(`${this.url}/admin/average/classroom/${classId}`, { observe: 'response' });
  }

  setCurrentQuestion(currentQuestion: number): void {
    this.currentQuestionSubject.next(currentQuestion);
  }

  watchCurrentQuestion(): Observable<number> {
    return this.currentQuestionSubject.asObservable();
  }


}

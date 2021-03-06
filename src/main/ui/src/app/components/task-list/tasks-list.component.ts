import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { TaskService } from '../../_services/task.service';

@Component({
  selector: 'app-tasks-list',
  templateUrl: './tasks-list.component.html',
  styleUrls: ['./tasks-list.component.css']
})
export class TasksListComponent implements OnInit {

  tasks: any;
  currentTask = null;
  currentIndex = -1;
  title = '';

  isTaskEditMode = false;

  showAdminBoard = false;
  showModeratorBoard = false;

  constructor(private taskService: TaskService,
              private tokenStorageService: TokenStorageService) { }

  ngOnInit(): void {
    this.retrieveTasks();

    if (!!this.tokenStorageService.getToken()) {
      const user = this.tokenStorageService.getUser();
      this.showAdminBoard = user.roles.includes('ROLE_ADMIN');
      this.showModeratorBoard = user.roles.includes('ROLE_MODERATOR');
    }
  }

  retrieveTasks(): void {
    this.taskService.getAll()
      .subscribe(
        data => {
          this.tasks = data;
          console.log(data);
        },
        error => {
          console.log(error);
        });
  }

  refreshList(): void {
    this.retrieveTasks();
    this.currentTask = null;
    this.currentIndex = -1;
  }

  setActiveTask(task, index): void {
    this.currentTask = task;
    this.currentIndex = index;
  }

  removeAllTasks(): void {
    this.taskService.deleteAll()
      .subscribe(
        response => {
          console.log(response);
          this.refreshList();
        },
        error => {
          console.log(error);
        });
  }

  searchTitle(): void {
    this.taskService.findByTitle(this.title)
      .subscribe(
        data => {
          this.tasks = data;
          console.log(data);
        },
        error => {
          console.log(error);
        });
  }

  onTaskCreate(): void{
    this.retrieveTasks();
    this.isTaskEditMode = false;
  }

}

import psycopg2
import pandas as pd
import dash
from dash.dependencies import Input, Output
from dash import dcc, html, dash_table
import pandas as pd
import plotly.express as px
import dash_bootstrap_components as dbc
import datetime
from datetime import date, timedelta
from dateutil import parser

import sklearn.metrics as metrics

import numpy as np

from os import environ as env, name
from dotenv import load_dotenv

load_dotenv()

for_dropdown = []

centers = {
    "CSC": { "name": "California Service Center", "currently_processing": [], "read_at": [] },
    "VSC": { "name": "Vermont Service Center", "currently_processing": [], "read_at": [] },
    "NSC": { "name": "Nebraska Service Center", "currently_processing": [], "read_at": [] },
    "TSC": { "name": "Texas Service Center", "currently_processing": [], "read_at": [] },
    "NBC": { "name": "National Benefits Center", "currently_processing": [], "read_at": [] },
}

#establishing the connection
conn = psycopg2.connect(
   database="postgres", user=env['DB_USERNAME'], password=env['DB_PASSWORD'], host='35.222.180.129'
)
#Creating a cursor object using the cursor() method
cursor = conn.cursor()


for_df = {"name": [], "currently_processing": [], "read_at": []}
for k in centers.keys():
    for_dropdown.append(centers[k]["name"])
    cursor.execute(f"SELECT currently_handling, read_at FROM uscis_stats WHERE center = '{k}'")
    data = cursor.fetchall()
    for d in data:
        for_df["name"].append(centers[k]['name'])
        for_df["currently_processing"].append(d[0])
        for_df["read_at"].append(d[1])

df = pd.DataFrame(for_df)
df['read_at'] = pd.to_datetime(df['read_at'])
df['currently_processing'] = pd.to_datetime(df['currently_processing'])

#Closing the connection
conn.close()



app = dash.Dash(__name__, external_stylesheets=[dbc.themes.BOOTSTRAP])
server = app.server

app.config.suppress_callback_exceptions = True

centers_for_dropdown = list(df['name'].unique())


# ------------------------------------------------------------------------------
# App layout
app.layout = dbc.Container([

    html.H3("Service Centers: Currently Processing", style={'textAlign': 'center'}),

    html.Div(
    [
        dbc.Row(
            [
                dbc.Col([

                        html.Div("Choose a service center"),
                        dcc.Dropdown(id="slct_ss",
                                    
                                    options=[{'label':center, 'value':center} for center in centers_for_dropdown],
                                    multi=False,
                                    value=centers_for_dropdown[0]
                                    ),
                ],
                width=12)
            ]
        )
    ]
    ),


    html.Br(),
    html.Div([
        html.Div("When did you submit your paperwork?"),

        dcc.DatePickerSingle(
            id='submission_date',
            min_date_allowed=date(2020, 1, 1),
            max_date_allowed=date(2025, 1, 1),
            initial_visible_month=date(2022, 5, 5),
            date=date(2022, 8, 10)
        )
    ]),

    html.Div(
    [
        dbc.Row(
            [
                dbc.Col([
                        dcc.Graph(id='progress_scatter', figure={}),
                ],
                width=12)
            ]
        ),
        dbc.Row(
            [
                dbc.Col([
                        html.Div(id="progress_summary", children=[])
                ],
                width=12)
            ]
        )
    ]),

])


# ------------------------------------------------------------------------------
# Connect the Plotly graphs with Dash Components
@app.callback(
    [Output(component_id='progress_scatter', component_property='figure'), Output(component_id="progress_summary", component_property="children")],
    [Input(component_id='slct_ss', component_property='value'), Input(component_id='submission_date', component_property='date')]
)
def update_graph(center, submission_date):

    if submission_date is not None:

        date_object = date.fromisoformat(submission_date)
        date_string = date_object.strftime('%Y-%m-%d')
        
        filtered_df = df[df['name']==center]
        read_at_max_for_x_range = filtered_df['read_at'].max() + timedelta(days=14)
        ramfxr_dt = filtered_df['read_at'].max() + timedelta(days=14)

        read_at = (filtered_df['read_at'] - datetime.datetime(2023, 5, 30, 0, 0, 0)).dt.days
        currently_processing = (datetime.datetime(date_object.year, date_object.month, date_object.day) - filtered_df['currently_processing']).dt.days

        range_x = [0, ( ramfxr_dt - datetime.datetime(2023, 5, 30, 0, 0, 0)).days]
        range_y = [0, (datetime.datetime(date_object.year, date_object.month, date_object.day) - filtered_df['currently_processing'].min()).days]
        range_x_dates = pd.date_range(datetime.datetime(2023, 5, 30, 0, 0, 0), periods=range_x[1]).tolist()
        range_y_dates = pd.date_range(filtered_df['currently_processing'].min(), periods=range_y[1]).tolist()
        scatter_trend_fig = px.scatter(
            x=list(read_at),
            y=list(currently_processing),
            range_x=range_x,
            range_y=range_y,
            trendline='ols',
            title=f"Progress at {list(filtered_df['name'].values)[0]}"
        )

        summary = px.get_trendline_results(scatter_trend_fig).iloc[0]["px_fit_results"].summary()
        results_as_html = summary.tables[1].as_html()
        v = pd.read_html(results_as_html, header=0, index_col=0)[0]
        constant = v['coef']['const']
        x1 = v['coef']['x1']

        x_intercept=(0-constant)/x1
        y_intercept = constant
        print(x_intercept)
        print(y_intercept)

        

        scatter_trend_fig.update_layout(
            shapes = [{'type': 'line',
                        'y0': y_intercept, 
                        'y1': 0, 
                        'x0': 0, 
                        'x1': x_intercept,
                        'line' : {
                            'color': 'Grey',
                            'dash': 'dash',
                            },
                        'label': {
                            'text': 'Trend line'
                            }
                }])

        ramfxr_dt = filtered_df['read_at'].max() + timedelta(days=int(x_intercept) + 5)
        range_x = [0, ( ramfxr_dt - datetime.datetime(2023, 5, 30, 0, 0, 0)).days]
        range_x_dates = pd.date_range(datetime.datetime(2023, 5, 30, 0, 0, 0), periods=range_x[1]).tolist()

        scatter_trend_fig.update_layout(
            xaxis_title="Date of most recent data", 
            yaxis_title="Currently processing"
        )

        scatter_trend_fig.update_xaxes(tickangle=45,
                 range=range_x,
                 tickmode = 'array',
                 tickvals = [x for x in range(0, range_x[1])][1::5],
                 ticktext = [pd.to_datetime(str(d)).strftime("%Y-%m-%d") for d in range_x_dates][1::5],
        )

        tt = [pd.to_datetime(str(d)).strftime("%Y-%m-%d") for d in range_y_dates][1::5]
        tt.reverse()

        scatter_trend_fig.update_yaxes(
                tickmode = 'array',
                tickvals = [x for x in range(0, range_y[1])][1::5],
                ticktext = tt,
        )
        
        progress_summary = f'Based on data from May 30th 2023 to the present, the {list(filtered_df["name"].values)[0]} will probably start processing your paperwork on {[pd.to_datetime(str(d)).strftime("%Y-%m-%d") for d in range_x_dates][int(x_intercept)]}'
        return scatter_trend_fig, progress_summary
    return dash.no_update, dash.no_update
    

# -------------------------- MAIN ---------------------------- #


if __name__ == '__main__':
    app.run_server(host='0.0.0.0', port=8081, debug=True, use_reloader=False)
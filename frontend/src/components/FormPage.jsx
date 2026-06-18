import AuthPanel from './AuthPanel'

function FormPage({ panelTitle, panelSubtitle, children }) {
  return (
    <div className="min-h-screen flex">
      <AuthPanel title={panelTitle} subtitle={panelSubtitle} />
      <div className="flex-1 flex flex-col justify-center px-8 md:px-14 bg-white overflow-y-auto py-10">
        <div className="max-w-sm w-full mx-auto">
          {children}
        </div>
      </div>
    </div>
  )
}

export default FormPage
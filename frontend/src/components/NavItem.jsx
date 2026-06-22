import { useNavigate } from 'react-router-dom'
import { forwardRef } from 'react'

const NavItem = forwardRef(function NavItem({ path, label, icon, active }, ref) {
  const navigate = useNavigate()
  return (
    <div
      ref={ref}
      onClick={() => navigate(path)}
      className={`flex items-center gap-2 px-3 py-2 rounded-lg cursor-pointer text-sm transition
        ${active
          ? 'bg-slate-800 text-white'
          : 'text-slate-500 hover:text-white hover:bg-slate-800'
        }`}
    >
      <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
        <path strokeLinecap="round" strokeLinejoin="round" d={icon} />
      </svg>
      {label}
    </div>
  )
})

export default NavItem